package nurisezgin.com.retrofiterror.rx2;

import nurisezgin.com.retrofiterror.SessionLogoutAction;
import okhttp3.Request;

/**
 * Created by nurisezgin on 28/07/2018.
 */
public class TestSessionLogoutActions{

    public static class EmptyAction implements SessionLogoutAction {

        @Override
        public void onSessionLoggedOut(Request request) { }
    }

    public static class NoEmptyConstructorAction implements SessionLogoutAction {

        private final Object object;

        public NoEmptyConstructorAction(Object object) {
            this.object = object;
        }

        @Override
        public void onSessionLoggedOut(Request request) {

        }
    }


}
