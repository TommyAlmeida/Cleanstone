package rocks.cleanstone.player.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rocks.cleanstone.net.utils.ByteBufUtils;
import rocks.cleanstone.player.Player;

public class PlayerDataKeyFactory {
    private PlayerDataKeyFactory() {
    }

    public static ByteBuf create(Player player, PlayerDataType type) {
        final ByteBuf buf = Unpooled.buffer();
        ByteBufUtils.writeUUID(buf, player.getId().getUUID());
        ByteBufUtils.writeVarInt(buf, type.getTypeID());
        return buf;
    }
}
