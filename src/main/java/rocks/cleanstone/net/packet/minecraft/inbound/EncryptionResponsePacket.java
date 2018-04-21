package rocks.cleanstone.net.packet.minecraft.inbound;

import rocks.cleanstone.net.packet.PacketType;
import rocks.cleanstone.net.packet.minecraft.MinecraftInboundPacketType;

public class EncryptionResponsePacket {

    private final byte[] sharedSecret;
    private final byte[] verifyToken;

    public EncryptionResponsePacket(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public PacketType getType() {
        return MinecraftInboundPacketType.ENCRYPTION_RESPONSE;
    }
}
