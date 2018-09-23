package rocks.cleanstone.net.minecraft.protocol.v1_12_2.outbound;

import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;
import rocks.cleanstone.net.minecraft.packet.outbound.LoginSuccessPacket;
import rocks.cleanstone.net.protocol.PacketCodec;
import rocks.cleanstone.net.utils.ByteBufUtils;

import java.io.IOException;

@Component
public class LoginSuccessCodec implements PacketCodec<LoginSuccessPacket> {

    @Override
    public LoginSuccessPacket decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException("LoginSuccess is outbound and cannot be decoded");
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, LoginSuccessPacket packet) throws IOException {

        ByteBufUtils.writeUTF8(byteBuf, packet.getUUID().toString());
        ByteBufUtils.writeUTF8(byteBuf, packet.getUserName());
        return byteBuf;
    }
}
