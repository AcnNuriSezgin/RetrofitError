package nurisezgin.com.retrofiterror;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by nurisezgin on 28/07/2018.
 */
public final class DefaultErrorAdapter implements ErrorAdapter {

    static final int UN_AUTHORIZE_ERROR = 401;

    @Override
    public final Throwable onError(Call<?> call, Annotation[] annotations, Throwable throwable) {
        String message;

        if (throwable == null) {
            throwable = new NullPointerException();
        }

        if (call == null || annotations == null || annotations.length == 0) {
            throw new IllegalArgumentException("ErrorAdapter:onError method, please check arguments," +
                    " those are empty or null");
        }

        if (throwable instanceof HttpException) {
            int statusCode = ((HttpException) throwable).code();
            String httpStatusMessage = ((HttpException) throwable).message();
            Response<?> response = ((HttpException) throwable).response();

            if (statusCode == UN_AUTHORIZE_ERROR) {
                onSessionLogoutError(call, annotations);
            }

            message = RetrofitErrorPlugin.errorConverter()
                    .onHttpError(statusCode, httpStatusMessage, response);
        } else if (throwable instanceof IOException) {
            message = RetrofitErrorPlugin.errorConverter()
                    .onIOError(throwable);
        } else {
            message = RetrofitErrorPlugin.errorConverter()
                    .onUnknownError(throwable);
        }

        return new RetrofitError(message);
    }

    private void onSessionLogoutError(Call<?> call, Annotation[] annotations) {
        DoOnSessionLogout customAction = tryToFindCustomAction(annotations);
        if (customAction != null) {
            Class<? extends SessionLogoutAction> action = customAction.value();
            try {
                action.newInstance().onSessionLoggedOut(call.request());
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RetrofitErrorPlugin.defaultSessionLogoutAction()
                .onSessionLoggedOut(call.request());
    }

    private DoOnSessionLogout tryToFindCustomAction(Annotation[] annotations) {
        DoOnSessionLogout customAction = null;

        for (Annotation ann : annotations) {
            if (ann instanceof DoOnSessionLogout) {
                customAction = (DoOnSessionLogout) ann;
                break;
            }
        }

        return customAction;
    }

}
