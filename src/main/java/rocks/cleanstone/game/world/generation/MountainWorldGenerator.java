package rocks.cleanstone.game.world.generation;

import java.util.HashSet;
import org.springframework.stereotype.Component;
import rocks.cleanstone.game.block.Block;
import rocks.cleanstone.game.block.ImmutableBlock;
import rocks.cleanstone.game.material.block.vanilla.VanillaBlockType;
import rocks.cleanstone.game.world.chunk.ArrayBlockDataTable;
import rocks.cleanstone.game.world.chunk.BlockDataTable;
import rocks.cleanstone.game.world.chunk.Chunk;
import rocks.cleanstone.game.world.chunk.SimpleChunk;
import rocks.cleanstone.game.world.chunk.data.block.vanilla.DirectPalette;
import rocks.cleanstone.game.world.chunk.data.block.vanilla.VanillaBlockDataStorage;
import rocks.cleanstone.game.world.chunk.data.block.vanilla.VanillaBlockDataStorageFactory;
import rocks.cleanstone.game.world.generation.utils.NoiseGenerator;
import rocks.cleanstone.net.minecraft.protocol.v1_13.ProtocolBlockStateMapping;
import rocks.cleanstone.net.packet.enums.Dimension;
import rocks.cleanstone.net.packet.enums.LevelType;

@Component
public class MountainWorldGenerator extends AbstractWorldGenerator {

    private boolean initialized;
    private Block GRASS_BLOCK;
    private Block DIRT;
    private Block STONE;
    private Block BEDROCK;
    private NoiseGenerator noiseGenerator;
    private final VanillaBlockDataStorageFactory vanillaBlockDataStorageFactory;
    private final ProtocolBlockStateMapping blockStateMapping;


    public MountainWorldGenerator(
            VanillaBlockDataStorageFactory vanillaBlockDataStorageFactory,
            ProtocolBlockStateMapping blockStateMapping

    ) {
        super(Dimension.OVERWORLD, LevelType.DEFAULT);
        this.vanillaBlockDataStorageFactory = vanillaBlockDataStorageFactory;
        this.blockStateMapping = blockStateMapping;

        noiseGenerator = new NoiseGenerator();
        noiseGenerator.SetNoiseType(NoiseGenerator.NoiseType.SimplexFractal);
        noiseGenerator.SetFrequency(0.0125F);
        noiseGenerator.SetFractalOctaves(3);
        noiseGenerator.SetFractalGain(0.35F);
        noiseGenerator.SetFractalLacunarity(3.5F);
    }

    public void initialize() {
        GRASS_BLOCK = ImmutableBlock.of(VanillaBlockType.GRASS_BLOCK);
        DIRT = ImmutableBlock.of(VanillaBlockType.DIRT);
        STONE = ImmutableBlock.of(VanillaBlockType.STONE);
        BEDROCK = ImmutableBlock.of(VanillaBlockType.BEDROCK);
        initialized = true;
    }

    @Override
    public Chunk generateChunk(int seed, int chunkX, int chunkY) {
        if (!initialized) {
            initialize();
        }

        noiseGenerator.SetSeed(seed);
        BlockDataTable blockDataTable = new ArrayBlockDataTable(true);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = getHeightAt(seed, (chunkX << 4) + x, (chunkY << 4) + z);
                blockDataTable.setBlock(x, height, z, GRASS_BLOCK);
                blockDataTable.setBlock(x, height - 1, z, DIRT);
                blockDataTable.setBlock(x, height - 2, z, DIRT);

                for (int y = 1; y < (height - 2); y++) {
                    blockDataTable.setBlock(x, y, z, STONE);
                }

                blockDataTable.setBlock(x, 0, z, BEDROCK);
                for (int y = 0; y < Chunk.HEIGHT; y++) {
                    blockDataTable.setSkyLight(x, y, z, (byte) 15);
                }
            }
        }
        DirectPalette directPalette = new DirectPalette(blockStateMapping, 14);
        VanillaBlockDataStorage blockDataStorage = vanillaBlockDataStorageFactory.get(blockDataTable,
                directPalette, true);

        return new SimpleChunk(blockDataTable, blockDataStorage, new HashSet<>(), chunkX, chunkY);
    }

    public int getHeightAt(int seed, int x, int z) {
        noiseGenerator.SetSeed(seed);
        return (int) Math.pow(((noiseGenerator.GetNoise(x, z) + 1.0) / 2.0) * 128.0, 1.0);
    }

    @Override
    public String getName() {
        return "mountainWorldGenerator";
    }
}
