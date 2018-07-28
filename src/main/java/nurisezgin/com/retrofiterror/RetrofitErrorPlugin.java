package nurisezgin.com.retrofiterror;

/**
 * Created by nurisezgin on 27/07/2018.
 */
public final class RetrofitErrorPlugin {

    private static SessionLogoutAction sessionLogoutAction = new SessionLogoutAction.Empty();
    private static ErrorConverter errorConverter = new ErrorConverter.Empty();
    private static ErrorAdapter errorAdapter = new DefaultErrorAdapter();

    private RetrofitErrorPlugin() { }

    public static void setDefaultSessionLogoutAction(SessionLogoutAction action) {
        sessionLogoutAction = action;
    }

    public static SessionLogoutAction defaultSessionLogoutAction() {
        return sessionLogoutAction;
    }

    public static void setErrorConverter(ErrorConverter errorConverter) {
        RetrofitErrorPlugin.errorConverter = errorConverter;
    }

    public static ErrorConverter errorConverter() {
        return errorConverter;
    }

    public static void setErrorAdapter(ErrorAdapter errorAdapter) {
        RetrofitErrorPlugin.errorAdapter = errorAdapter;
    }

    public static ErrorAdapter errorAdapter() {
        return errorAdapter;
    }

}
