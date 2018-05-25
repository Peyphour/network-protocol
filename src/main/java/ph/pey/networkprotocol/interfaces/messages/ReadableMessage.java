package ph.pey.networkprotocol.interfaces.messages;

import java.io.IOException;

/**
 * Created by bertrand on 25/01/17.
 */
public interface ReadableMessage extends NetworkMessage {
    NetworkMessage read(byte[] content) throws IOException;
}
