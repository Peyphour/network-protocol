package ph.pey.networkprotocol.interfaces;

import ph.pey.networkprotocol.interfaces.messages.NetworkMessage;
import ph.pey.networkprotocol.interfaces.messages.WritableMessage;

/**
 * Created by bertrand on 25/01/17.
 */
public interface AbstractAction {

    boolean hasMessage();

    default WritableMessage getActionMessage() {
        return null;
    }

    boolean hasNextAction();

    default Class<? extends AbstractAction> getNextAction() {
        return null;
    }

    void processMessage(NetworkMessage message);
}
