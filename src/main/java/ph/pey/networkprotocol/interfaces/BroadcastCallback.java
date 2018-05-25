package ph.pey.networkprotocol.interfaces;


/**
 * Created by bertrand on 27/01/17.
 */
public interface BroadcastCallback {

    void handle(Class<? extends BroadcastableAction> classz);
}
