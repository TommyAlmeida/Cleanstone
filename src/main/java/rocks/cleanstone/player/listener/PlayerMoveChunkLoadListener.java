package rocks.cleanstone.player.listener;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import rocks.cleanstone.game.Position;
import rocks.cleanstone.game.world.chunk.ChunkCoords;
import rocks.cleanstone.game.world.chunk.NearbyChunkRetriever;
import rocks.cleanstone.player.Player;
import rocks.cleanstone.player.PlayerChunkLoadService;
import rocks.cleanstone.player.event.PlayerMoveEvent;
import rocks.cleanstone.player.event.PlayerQuitEvent;

@Slf4j
@Component
public class PlayerMoveChunkLoadListener {

    private final Map<UUID, AtomicInteger> updateCounterMap = new ConcurrentHashMap<>();
    private final PlayerChunkLoadService playerChunkLoadService;
    private final NearbyChunkRetriever nearbyChunkRetriever;

    @Autowired
    public PlayerMoveChunkLoadListener(PlayerChunkLoadService playerChunkLoadService, NearbyChunkRetriever nearbyChunkRetriever) {
        this.playerChunkLoadService = playerChunkLoadService;
        this.nearbyChunkRetriever = nearbyChunkRetriever;
    }

    @Async("playerExec")
    @EventListener
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        final ChunkCoords coords = ChunkCoords.of(playerMoveEvent.getNewPosition());

        final Player player = playerMoveEvent.getPlayer();
        final UUID uuid = player.getId().getUUID();

        // reject unneeded updates early
        if (chunkUpdateNotNeeded(playerMoveEvent, coords, uuid)) {
            return;
        }

        final int loadingToken = acquireLoadingToken(uuid);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (uuid) {
            // return early
            if (shouldAbortLoading(uuid, loadingToken)) {
                return;
            }
            // check again because the chunk could already have been loaded inside synchronized block
            if (chunkUpdateNotNeeded(playerMoveEvent, coords, uuid)) {
                return;
            }

            log.debug("loading chunks around {} for {}", coords, player.getId().getName());
            sendNewNearbyChunks(player, coords, loadingToken);
            unloadRemoteChunks(player, coords);
        }
    }

    @Async("playerExec")
    @EventListener
    public void onPlayerDisconnect(PlayerQuitEvent playerQuitEvent) {
        playerUnloadAll(playerQuitEvent.getPlayer().getId().getUUID());
    }

    protected boolean chunkUpdateNotNeeded(PlayerMoveEvent playerMoveEvent, ChunkCoords coords, UUID uuid) {
        return isSameChunk(playerMoveEvent.getOldPosition(), playerMoveEvent.getNewPosition())
                && playerChunkLoadService.hasPlayerLoaded(uuid, coords);
    }

    protected void sendNewNearbyChunks(Player player, ChunkCoords playerCoords, int loadingToken) {
        final int sendDistance = player.getViewDistance() + 1;
        final UUID uuid = player.getId().getUUID();

        final Collection<ChunkCoords> nearbyCoords = nearbyChunkRetriever.getChunkCoordsAround(playerCoords, sendDistance);

        for (final ChunkCoords coords : nearbyCoords) {
            if (shouldAbortLoading(uuid, loadingToken)) {
                log.debug("aborted loading chunks for {}", player.getId().getName());
                return;
            }

            playerChunkLoadService.loadChunk(player, coords);
        }

        log.debug("done loading chunks for {}", player.getId().getName());
    }


    protected void unloadRemoteChunks(Player player, ChunkCoords playerCoords) {
        final int sendDistance = player.getViewDistance() + 1;
        final UUID uuid = player.getId().getUUID();

        playerChunkLoadService.getLoadedChunkCoords(uuid).stream()
                .filter(chunk -> !isWithinRange(playerCoords.getX(), playerCoords.getZ(), chunk.getX(), chunk.getZ(), sendDistance))
                .forEach(coords -> {
                    // use specific functions, because it is certain that the player has loaded these chunks
                    playerChunkLoadService.unregisterLoadedChunk(uuid, coords);
                    playerChunkLoadService.sendChunkUnloadPacket(player, coords);
                });
    }

    protected boolean isWithinRange(int x1, int z1, int x2, int z2, int range) {
        return Math.abs(x2 - x1) <= range && Math.abs(z2 - z1) <= range;
    }

    private boolean isSameChunk(Position oldPosition, Position newPosition) {
        return ChunkCoords.of(oldPosition).equals(ChunkCoords.of(newPosition));
    }

    protected int acquireLoadingToken(UUID uuid) {
        return updateCounterMap.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
    }

    protected boolean shouldAbortLoading(UUID uuid, int loadingToken) {
        return !updateCounterMap.containsKey(uuid) || updateCounterMap.get(uuid).get() != loadingToken;
    }

    protected void playerUnloadAll(UUID uuid) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (uuid) {
            playerChunkLoadService.unregisterAllLoadedChunks(uuid);
            updateCounterMap.remove(uuid);
        }
    }
}