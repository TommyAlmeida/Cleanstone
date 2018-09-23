package rocks.cleanstone.net.minecraft.protocol.v1_13.inbound;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rocks.cleanstone.game.inventory.item.ItemStack;
import rocks.cleanstone.game.inventory.item.SimpleItemStack;
import rocks.cleanstone.game.material.item.ItemType;
import rocks.cleanstone.game.material.item.mapping.ItemTypeMapping;
import rocks.cleanstone.net.minecraft.packet.inbound.CreativeInventoryActionPacket;
import rocks.cleanstone.net.protocol.PacketCodec;

import javax.annotation.Nullable;

@Component
public class CreativeInventoryActionCodec implements PacketCodec<CreativeInventoryActionPacket> {

    private final ItemTypeMapping<Integer> itemTypeMapping;

    public CreativeInventoryActionCodec(@Qualifier("protocolItemTypeMapping_v1_13") ItemTypeMapping<Integer> itemTypeMapping) {
        this.itemTypeMapping = itemTypeMapping;

    }

    @Override
    public CreativeInventoryActionPacket decode(ByteBuf byteBuf) {
        short slot = byteBuf.readShort();
        ItemStack clickedItem = readItemStack(byteBuf);

        return new CreativeInventoryActionPacket(slot, clickedItem);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, CreativeInventoryActionPacket packet) {
        throw new UnsupportedOperationException("CreativeInventoryAction is inbound and cannot be encoded");
    }

    @Nullable
    private ItemStack readItemStack(ByteBuf byteBuf) {
        short itemID = byteBuf.readShort();

        if (itemID != -1) {
            byte itemCount = byteBuf.readByte();
            ItemType itemType = itemTypeMapping.getItemType((int) itemID);
            Preconditions.checkNotNull(itemType, "Cannot find itemType with ID " + itemID);
            byte nbtStartByte = byteBuf.readByte(); // TODO Item NBT
            return new SimpleItemStack(itemType, itemCount, null);
        }
        return null;
    }
}
