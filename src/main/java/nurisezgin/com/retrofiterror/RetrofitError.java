package nurisezgin.com.retrofiterror;

/**
 * Created by nurisezgin on 28/07/2018.
 */
public final class RetrofitError extends Exception {

    private final String message;

    public RetrofitError(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
