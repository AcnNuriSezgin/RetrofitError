package nurisezgin.com.retrofiterror;

import retrofit2.Response;

/**
 * Created by nurisezgin on 27/07/2018.
 */
public interface ErrorConverter {

    String onHttpError(int code, String responseBody, Response<?> response);

    String onIOError(Throwable throwable);

    String onUnknownError(Throwable throwable);

    final class Empty implements ErrorConverter {

        @Override
        public String onHttpError(int code, String responseBody, Response<?> response) {
            return "";
        }

        @Override
        public String onIOError(Throwable throwable) {
            return "";
        }

        @Override
        public String onUnknownError(Throwable throwable) {
            return "";
        }
    }

}
