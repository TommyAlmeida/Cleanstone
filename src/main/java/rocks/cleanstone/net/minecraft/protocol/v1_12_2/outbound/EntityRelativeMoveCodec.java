package rocks.cleanstone.net.minecraft.protocol.v1_12_2.outbound;

import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;
import rocks.cleanstone.net.minecraft.packet.outbound.EntityRelativeMovePacket;
import rocks.cleanstone.net.protocol.PacketCodec;
import rocks.cleanstone.net.utils.ByteBufUtils;

@Component
public class EntityRelativeMoveCodec implements PacketCodec<EntityRelativeMovePacket> {

    @Override
    public EntityRelativeMovePacket decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException("EntityRelativeMove is outbound and cannot be decoded");
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, EntityRelativeMovePacket packet) {

        ByteBufUtils.writeVarInt(byteBuf, packet.getEntityID());
        byteBuf.writeShort(packet.getDeltaX());
        byteBuf.writeShort(packet.getDeltaY());
        byteBuf.writeShort(packet.getDeltaZ());
        byteBuf.writeBoolean(packet.isOnGround());

        return byteBuf;
    }
}
