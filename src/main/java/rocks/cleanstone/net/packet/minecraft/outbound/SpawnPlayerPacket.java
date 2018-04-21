package rocks.cleanstone.net.packet.minecraft.outbound;

import rocks.cleanstone.net.packet.PacketType;
import rocks.cleanstone.net.packet.minecraft.MinecraftOutboundPacketType;
import rocks.cleanstone.net.packet.minecraft.data.EntityMetadata;

import java.util.UUID;

public class SpawnPlayerPacket {

    private final int entityID;
    private final UUID playerUUID;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final EntityMetadata entityMetadata;

    public SpawnPlayerPacket(int entityID, UUID playerUUID, double x, double y, double z, float yaw, float pitch, EntityMetadata entityMetadata) {
        this.entityID = entityID;
        this.playerUUID = playerUUID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.entityMetadata = entityMetadata;
    }

    public int getEntityID() {
        return entityID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    public PacketType getType() {
        return MinecraftOutboundPacketType.SPAWN_PLAYER;
    }
}
