package nurisezgin.com.retrofiterror.rx2;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import nurisezgin.com.retrofiterror.ErrorAdapter;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by nurisezgin on 27/07/2018.
 */
public final class Rx2RetrofitErrorCallAdapterFactory extends CallAdapter.Factory {

    private final CallAdapter.Factory originalSource;

    public Rx2RetrofitErrorCallAdapterFactory(boolean isAsync) {
        this.originalSource = isAsync
                ? RxJava2CallAdapterFactory.createAsync()
                : RxJava2CallAdapterFactory.create();
    }

    public static Rx2RetrofitErrorCallAdapterFactory create() {
        return new Rx2RetrofitErrorCallAdapterFactory(false);
    }

    public static Rx2RetrofitErrorCallAdapterFactory createAsync() {
        return new Rx2RetrofitErrorCallAdapterFactory(true);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<?, ?> originalCallAdapter = originalSource.get(returnType, annotations, retrofit);
        return new RetrofitErrorCallAdapter<>(annotations, originalCallAdapter);
    }

    private static class RetrofitErrorCallAdapter<R> implements CallAdapter<R, Object> {

        private final Annotation[] annotations;
        private final CallAdapter<R, ?> originalSource;

        public RetrofitErrorCallAdapter(Annotation[] annotations,
                                        CallAdapter<R, ?> originalSource) {

            this.annotations = annotations;
            this.originalSource = originalSource;
        }

        @Override
        public Type responseType() {
            return originalSource.responseType();
        }

        @Override
        public Object adapt(Call<R> call) {
            Call<R> copiedCall = call.clone();
            return ((Observable) originalSource.adapt(call))
                        .onErrorResumeNext(new ErrorMapper(copiedCall));
        }

        private class ErrorMapper implements Function<Throwable, ObservableSource> {

            private Call<R> call;

            public ErrorMapper(Call<R> call) {
                this.call = call;
            }

            @Override
            public Observable apply(@NonNull Throwable throwable) throws Exception {
                Throwable newThrowable = new ErrorAdapter()
                        .onError(call, annotations, throwable);

                return Observable.error(newThrowable);
            }
        }

    }


}
