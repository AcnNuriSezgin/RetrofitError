package nurisezgin.com.retrofiterror;

import org.junit.Test;
import retrofit2.Call;
import retrofit2.HttpException;

import java.lang.annotation.Annotation;

import static nurisezgin.com.retrofiterror.ErrorAdapter.UN_AUTHORIZE_ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by nurisezgin on 28/07/2018.
 */
public class ErrorAdapterTest {

    @Test (expected = IllegalArgumentException.class)
    public void should_EmptyAnnotationsFireExceptionCorrect() {
        HttpException httpException = mock(HttpException.class);
        when(httpException.code()).thenReturn(UN_AUTHORIZE_ERROR);

        Call mockCall = mock(Call.class);

        new ErrorAdapter()
                .onError(mockCall, null, httpException);
    }

    @Test (expected = IllegalArgumentException.class)
    public void should_EmptyCallObjectFireCorrect() {
        HttpException httpException = mock(HttpException.class);
        when(httpException.code()).thenReturn(UN_AUTHORIZE_ERROR);

        Annotation[] annotations = new Annotation[1];
        Annotation mockAnnotation = mock(Annotation.class);
        annotations[0] = mockAnnotation;

        new ErrorAdapter()
                .onError(null, annotations, httpException);
    }

    @Test
    public void should_NullThrowableReturnUnknownErrorCorrect() {
        final String expected = "Unexpected-Error";

        ErrorConverter mockErrorConverter = mock(ErrorConverter.class);
        when(mockErrorConverter.onUnknownError(any(Throwable.class)))
                .thenReturn(expected);
        RetrofitErrorPlugin.setErrorConverter(mockErrorConverter);

        Call mockCall = mock(Call.class);

        Annotation[] annotations = new Annotation[1];
        Annotation mockAnnotation = mock(Annotation.class);
        annotations[0] = mockAnnotation;

        Throwable throwable = new ErrorAdapter()
                .onError(mockCall, annotations, null);

        String actual = throwable.getMessage();
        assertThat(actual, is(equalTo(expected)));
    }

}