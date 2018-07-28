package nurisezgin.com.retrofiterror;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by nurisezgin on 27/07/2018.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface DoOnSessionLogout {

    Class<? extends SessionLogoutAction> value() default SessionLogoutAction.Empty.class;

}
