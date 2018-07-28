package nurisezgin.com.retrofiterror;

import okhttp3.Request;

/**
 * Created by nurisezgin on 27/07/2018.
 */
public interface SessionLogoutAction {

    void onSessionLoggedOut(Request request);

    final class Empty implements SessionLogoutAction {
        public void onSessionLoggedOut(Request request) { }
    }

}
