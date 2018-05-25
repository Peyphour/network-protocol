package ph.pey.networkprotocol.interfaces.messages;


import ph.pey.networkprotocol.annotations.Message;

/**
 * Created by bertrand on 24/01/17.
 */
public interface NetworkMessage {
    default int getId() {
        return this.getClass().getAnnotation(Message.class).id();
    }
}
