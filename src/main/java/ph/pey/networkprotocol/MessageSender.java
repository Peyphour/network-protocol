package ph.pey.networkprotocol;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ph.pey.networkprotocol.interfaces.messages.WritableMessage;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bertrand on 25/01/17.
 */
public class MessageSender {

    private OutputStream writeStream;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public MessageSender(OutputStream writeStream) {
        this.writeStream = writeStream;
    }

    public void send(WritableMessage networkMessage) {
        try {
            LOGGER.info("Sending message " + networkMessage.getClass().getSimpleName());
            networkMessage.write(writeStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
