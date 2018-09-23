package rocks.cleanstone.net.minecraft.protocol.v1_12_2.outbound;

import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;
import rocks.cleanstone.net.minecraft.packet.outbound.ChangeGameStatePacket;
import rocks.cleanstone.net.protocol.PacketCodec;

@Component
public class ChangeGameStateCodec implements PacketCodec<ChangeGameStatePacket> {

    @Override
    public ChangeGameStatePacket decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException("ChangeGameState is outbound and cannot be decoded");
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, ChangeGameStatePacket packet) {

        byteBuf.writeByte(packet.getReason().getReasonID());
        byteBuf.writeFloat(packet.getValue());

        return byteBuf;
    }
}
