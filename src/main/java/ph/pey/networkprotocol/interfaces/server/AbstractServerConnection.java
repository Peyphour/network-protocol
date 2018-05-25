package ph.pey.networkprotocol.interfaces.server;

import ph.pey.networkprotocol.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by bertrand on 25/01/17.
 */
public interface AbstractServerConnection extends Runnable {

    void stop();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    default Packet parsePacket(InputStream inputStream) throws IOException {

        Packet packet = new Packet();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        byte[] header = new byte[2];
        dataInputStream.readFully(header);

        ByteBuffer byteBuffer = ByteBuffer.wrap(header);
        int packetId = byteBuffer.getShort() >> 2;

        int packetLengthBytes = ((header[0] << 8 | header[1])) & 0b11;
        int packetLength = 0;

        byte[] packetLengthBuffer = new byte[packetLengthBytes];
        dataInputStream.readFully(packetLengthBuffer);

        for (int i = 0; i < packetLengthBytes; i++) {
            packetLength |= packetLengthBuffer[i] << ((packetLengthBytes - i - 1) * 8);
        }

        if(packetLength < 0) {
            packet.packetId = -1;
            return packet;
        }

        byte[] packetContent = new byte[packetLength];

        dataInputStream.readFully(packetContent);

        packet.packetId = packetId;
        packet.packetContent = packetContent;

        return packet;
    }
}
