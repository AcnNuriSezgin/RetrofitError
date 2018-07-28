package nurisezgin.com.retrofiterror.rx2;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import nurisezgin.com.retrofiterror.DoOnSessionLogout;
import nurisezgin.com.retrofiterror.ErrorConverter;
import nurisezgin.com.retrofiterror.RetrofitErrorPlugin;
import nurisezgin.com.retrofiterror.SessionLogoutAction;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

/**
 * Created by nurisezgin on 28/07/2018.
 */
public class Rx2RetrofitErrorCallAdapterFactoryTest {

    @Rule
    public MockWebServer webServer = new MockWebServer();

    private Service service;

    @Before
    public void setUp_() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(webServer.url("/"))
                .addConverterFactory(new ToStringConverterFactory())
                .addCallAdapterFactory(Rx2RetrofitErrorCallAdapterFactory.create())
                .build();

        service = retrofit.create(Service.class);
    }

    @Test
    public void should_Rx2ImplementationCorrect() throws InterruptedException {
        final String expected = "John";
        webServer.enqueue(new MockResponse().setBody(expected));

        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        service.getName()
                .subscribe(keepOnQueue(queue));

        String actual = queue.take();
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void should_WhenHasCustomActionNeverCallDefaultSessionLogoutActionCorrect() throws Exception {
        SessionLogoutAction mockAction = mock(SessionLogoutAction.class);
        RetrofitErrorPlugin.setDefaultSessionLogoutAction(mockAction);

        webServer.enqueue(new MockResponse().setResponseCode(401).setBody("Un authorized user!!!"));

        service.getUserBalance()
                .subscribe(this.<String>empty(), this.<Throwable>empty());

        verify(mockAction, never())
                .onSessionLoggedOut(any(Request.class));
    }

    @Test
    public void should_WhenNoEmptyConstructorCallDefaultSessionLogoutActionCorrect() throws Exception {
        SessionLogoutAction mockAction = mock(SessionLogoutAction.class);
        RetrofitErrorPlugin.setDefaultSessionLogoutAction(mockAction);

        webServer.enqueue(new MockResponse().setResponseCode(401).setBody("Un authorized user!!!"));

        service.getRawUserBalance()
                .subscribe(this.<String>empty(), this.<Throwable>empty());

        verify(mockAction)
                .onSessionLoggedOut(any(Request.class));
    }

    @Test
    public void should_CallDefaultSessionLogoutActionCorrect() {
        SessionLogoutAction mockAction = mock(SessionLogoutAction.class);
        RetrofitErrorPlugin.setDefaultSessionLogoutAction(mockAction);

        webServer.enqueue(new MockResponse().setResponseCode(401).setBody("Un authorized user!!!"));

        service.getUserFees()
                .subscribe(this.<String>empty(), this.<Throwable>empty());

        verify(mockAction)
                .onSessionLoggedOut(any(Request.class));
    }

    @Test
    public void should_ConvertHttpErrorCorrect() throws InterruptedException {
        final String expected = "Http-Error";

        ErrorConverter mockErrorConverter = mock(ErrorConverter.class);
        when(mockErrorConverter.onHttpError(anyInt(), anyString(), any(Response.class)))
                .thenReturn(expected);

        RetrofitErrorPlugin.setErrorConverter(mockErrorConverter);

        webServer.enqueue(new MockResponse().setResponseCode(404));

        final BlockingQueue<Throwable> queue = new ArrayBlockingQueue<>(1);

        service.getName()
                .subscribe(this.<String>empty(), keepOnQueue(queue));

        String actual = queue.take().getMessage();
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void should_ConvertIOErrorCorrect() throws InterruptedException {
        final String expected = "IO-Error";

        ErrorConverter mockErrorConverter = mock(ErrorConverter.class);
        when(mockErrorConverter.onIOError(any(Throwable.class)))
                .thenReturn(expected);

        RetrofitErrorPlugin.setErrorConverter(mockErrorConverter);

        webServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        final BlockingQueue<Throwable> queue = new ArrayBlockingQueue<>(1);

        service.getName()
                .subscribe(this.<String>empty(), keepOnQueue(queue));

        String actual = queue.take().getMessage();
        assertThat(actual, is(equalTo(expected)));
    }

    private <T> Consumer<T> keepOnQueue(final BlockingQueue<T> queue) {
        return new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                queue.offer(t);
            }
        };
    }

    private <T> Consumer<T> empty() {
        return new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception { }
        };
    }

    interface Service {

        @GET("/name")
        Observable<String> getName();

        @DoOnSessionLogout(TestSessionLogoutActions.EmptyAction.class)
        @GET("/balance")
        Observable<String> getUserBalance();

        @DoOnSessionLogout(TestSessionLogoutActions.NoEmptyConstructorAction.class)
        @GET("/rawbalance")
        Observable<String> getRawUserBalance();

        @GET("/fees")
        Observable<String> getUserFees();

    }

}