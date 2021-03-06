package rocks.cleanstone.endpoint.minecraft.vanilla.net.packet.inbound;

import rocks.cleanstone.endpoint.minecraft.vanilla.net.packet.MinecraftInboundPacketType;
import rocks.cleanstone.game.inventory.item.ItemStack;
import rocks.cleanstone.net.packet.Packet;
import rocks.cleanstone.net.packet.PacketType;

import javax.annotation.Nullable;

public class CreativeInventoryActionPacket implements Packet {

    private final short slot;
    private final ItemStack clickedItem;

    public CreativeInventoryActionPacket(short slot, @Nullable ItemStack clickedItem) {
        this.slot = slot;
        this.clickedItem = clickedItem;
    }

    public short getSlot() {
        return slot;
    }

    @Nullable
    public ItemStack getClickedItem() {
        return clickedItem;
    }

    @Override
    public PacketType getType() {
        return MinecraftInboundPacketType.CREATIVE_INVENTORY_ACTION;
    }
}
