package rocks.cleanstone.net.minecraft.protocol.v1_12_2.outbound;

import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;
import rocks.cleanstone.net.minecraft.packet.outbound.AnimationPacket;
import rocks.cleanstone.net.protocol.PacketCodec;
import rocks.cleanstone.net.utils.ByteBufUtils;

@Component
public class OutAnimationCodec implements PacketCodec<AnimationPacket> {

    @Override
    public AnimationPacket decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException("OutAnimation is outbound and cannot be decoded");
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, AnimationPacket packet) {

        ByteBufUtils.writeVarInt(byteBuf, packet.getEntityID());
        byteBuf.writeByte(packet.getAnimation().getAnimationID());

        return byteBuf;
    }
}
