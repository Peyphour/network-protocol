package ph.pey.networkprotocol;



import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ph.pey.networkprotocol.interfaces.AbstractAction;
import ph.pey.networkprotocol.annotations.Action;
import ph.pey.networkprotocol.interfaces.BroadcastCallback;
import ph.pey.networkprotocol.interfaces.BroadcastableAction;
import ph.pey.networkprotocol.interfaces.messages.NetworkMessage;
import ph.pey.networkprotocol.interfaces.messages.WritableMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bertrand on 25/01/17.
 */
public class MessageDispatcher {

    private MessageReceiver messageReceiver;
    private MessageSender messageSender;

    private OutputStream writeStream;

    private Map<Class<? extends NetworkMessage>, Class<? extends AbstractAction>> actionsRegistry;

    private BroadcastCallback broadcastCallback;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public MessageDispatcher(String actionsPackage, String messagesPackage) {
        try {
            this.messageReceiver = new MessageReceiver(messagesPackage);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        this.messageSender = new MessageSender(writeStream);

        this.actionsRegistry = new HashMap<>();

        this.initActionRegistry(actionsPackage);
    }

    @SuppressWarnings("unchecked")
    private void initActionRegistry(String actionsPackage) {
        Reflections reflections = new Reflections(actionsPackage);
        for (Class<?> classz : reflections.getTypesAnnotatedWith(Action.class)) {
            LOGGER.info("Adding action " + classz + " triggered by " + classz.getAnnotation(Action.class).trigger());
            this.actionsRegistry.put(classz.getAnnotation(Action.class).trigger(), (Class<? extends AbstractAction>) classz);
        }
    }

    public void handle(Packet packet) throws IllegalAccessException, IOException, InstantiationException {
        NetworkMessage parsedMessage = messageReceiver.handle(packet);
        if (parsedMessage != null) {
            LOGGER.info("Got packet : " + parsedMessage.toString());
            this.handleAction(this.actionsRegistry.get(parsedMessage.getClass()), parsedMessage);
        } else {
            if(packet.packetId > 0)
                LOGGER.info("Packet unknown : " + packet.packetId);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleAction(Class<? extends AbstractAction> actionClass, NetworkMessage parsedMessage) throws IllegalAccessException, InstantiationException {
        if (actionClass != null) {

            if(BroadcastableAction.class.isAssignableFrom(actionClass)) {
                broadcastCallback.handle((Class<? extends BroadcastableAction>) actionClass);
                return;
            }

            AbstractAction action = actionClass.newInstance();

            if (parsedMessage != null)
                action.processMessage(parsedMessage);

            if (action.hasMessage())
                this.send(action.getActionMessage());


            if (action.hasNextAction()) {
                this.handleAction(action.getNextAction(), null);
            }
        }
    }

    private void send(WritableMessage networkMessage) {
        this.messageSender.send(networkMessage);
    }

    public void setWriteStream(OutputStream writeStream) {
        this.writeStream = writeStream;
        this.messageSender = new MessageSender(writeStream);
    }

    public void registerBroadcastHandler(BroadcastCallback broadcastCallback) {
        this.broadcastCallback = broadcastCallback;
    }
}
