package ph.pey.networkprotocol.interfaces.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by bertrand on 25/01/17.
 */
public interface WritableMessage extends NetworkMessage {
    ByteArrayOutputStream serialize() throws IOException;


    default void write(OutputStream outputStream) throws IOException {
        ByteArrayOutputStream packetContent = this.serialize();

        ByteBuffer finalPacket = ByteBuffer.allocate(2 + this.computePacketLengthByte(packetContent.size()) + packetContent.size());
        finalPacket.put(computeHeader(packetContent.size()));
        switch (this.computePacketLengthByte(packetContent.size())) {
            case 1:
                finalPacket.put((byte) packetContent.size());
                break;
            case 2:
                finalPacket.putShort((short) packetContent.size());
                break;
            case 3:
                finalPacket.put((byte) ((packetContent.size() >> 16) & 0xFF));
                finalPacket.putShort((short) ((packetContent.size()) & 0xFFFF));
                break;
        }

        finalPacket.put(packetContent.toByteArray());

        outputStream.write(finalPacket.array());
    }

    default byte[] computeHeader(int packetLength) {
        short header = (short) (this.getId() << 2 | computePacketLengthByte(packetLength));
        return new byte[]{
                (byte) ((header >> 8) & 0xff),
                (byte) (header & 0xff)
        };
    }

    default byte computePacketLengthByte(int packetLength) {
        if (packetLength > 65535)
            return 3;
        else if (packetLength > 255)
            return 2;
        else if (packetLength > 0)
            return 1;
        else
            return 0;
    }
}
