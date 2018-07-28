package nurisezgin.com.retrofiterror;

import retrofit2.Call;

import java.lang.annotation.Annotation;

/**
 * Created by nurisezgin on 29/07/2018.
 */
public interface ErrorAdapter {

    Throwable onError(Call<?> call, Annotation[] annotations, Throwable throwable);

}
