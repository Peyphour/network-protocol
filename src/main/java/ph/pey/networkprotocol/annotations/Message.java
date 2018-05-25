package ph.pey.networkprotocol.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by bertrand on 26/01/17.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {
    int id();
}
