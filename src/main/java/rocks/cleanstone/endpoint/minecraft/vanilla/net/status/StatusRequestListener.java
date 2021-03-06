package rocks.cleanstone.endpoint.minecraft.vanilla.net.status;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import rocks.cleanstone.endpoint.minecraft.vanilla.net.packet.inbound.RequestPacket;
import rocks.cleanstone.endpoint.minecraft.vanilla.net.packet.outbound.ResponsePacket;
import rocks.cleanstone.game.config.GameConfig;
import rocks.cleanstone.net.event.InboundPacketEvent;
import rocks.cleanstone.net.protocol.ClientProtocolLayer;
import rocks.cleanstone.player.PlayerManager;

@Component
public class StatusRequestListener {

    private final String motd;
    private final int maxPlayers;
    private final PlayerManager playerManager;

    @Autowired
    public StatusRequestListener(GameConfig gameConfig, PlayerManager playerManager) {
        this.motd = gameConfig.getMotd();
        this.maxPlayers = gameConfig.getMaxPlayers();
        this.playerManager = playerManager;
    }

    @Async
    @EventListener
    public void onReceive(InboundPacketEvent<RequestPacket> event) {
        ClientProtocolLayer latestSupportedClientVersion = event.getConnection().getClientProtocolLayer();

        StatusResponse statusResponse = new StatusResponse(
                new StatusResponse.Version(latestSupportedClientVersion.getName(),
                        latestSupportedClientVersion.getOrderedVersionNumber()),
                new StatusResponse.Players(maxPlayers, playerManager.getOnlinePlayers().size(),
                        new StatusResponse.Players.SampleItem[]{}),
                new StatusResponse.Description(motd));
        String jsonResponse = new Gson().toJson(statusResponse);

        event.getConnection().sendPacket(new ResponsePacket(jsonResponse));
    }
}
