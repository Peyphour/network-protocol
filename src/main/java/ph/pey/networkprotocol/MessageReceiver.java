package ph.pey.networkprotocol;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ph.pey.networkprotocol.annotations.Message;
import ph.pey.networkprotocol.interfaces.messages.NetworkMessage;
import ph.pey.networkprotocol.interfaces.messages.ReadableMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bertrand on 24/01/17.
 */
public class MessageReceiver {

    private Map<Integer, Class<? extends ReadableMessage>> messages;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public MessageReceiver(String messagesPackage) throws IllegalAccessException, InstantiationException {
        messages = new HashMap<>();

        Reflections reflections = new Reflections(messagesPackage);

        for (Class<?> classz : reflections.getTypesAnnotatedWith(Message.class)) {
            LOGGER.info("Registering message " + classz);
            messages.put(classz.getAnnotation(Message.class).id(), (Class<? extends ReadableMessage>) classz);
        }
    }

    public NetworkMessage handle(Packet packet) throws IllegalAccessException, InstantiationException, IOException {
        if (packet != null && messages.get(packet.packetId) != null)
            return messages.get(packet.packetId).newInstance().read(packet.packetContent);

        return null;
    }
}
