package com.example.backrooms94.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Small chunk-local, entity-free toy-town props. */
public class TownFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState ROAD = Blocks.GRAY_CONCRETE.defaultBlockState();
    private static final BlockState WALL = Blocks.WHITE_TERRACOTTA.defaultBlockState();
    private static final BlockState ROOF = Blocks.RED_TERRACOTTA.defaultBlockState();
    private static final BlockState FLOOR = Blocks.OAK_PLANKS.defaultBlockState();

    public TownFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        // Keep all writes inside the source chunk so neighbouring chunks remain deterministic.
        int baseX = (origin.getX() & ~15) + 6 + random.nextInt(4);
        int baseZ = (origin.getZ() & ~15) + 6 + random.nextInt(4);
        int groundY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, baseX, baseZ);
        if (groundY < level.getMinBuildHeight() + 8 || groundY > 220) return false;
        if (random.nextInt(5) == 0) buildFountain(level, baseX, groundY, baseZ);
        else buildHouse(level, baseX, groundY, baseZ, random);
        return true;
    }

    private void buildHouse(WorldGenLevel level, int x, int y, int z, RandomSource random) {
        // A simple 1930s-inspired, hand-built cottage with a red pitched roof.
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            set(level, x + dx, y - 1, z + dz, FLOOR);
        }
        for (int dy = 0; dy <= 2; dy++) {
            for (int d = -2; d <= 2; d++) {
                set(level, x + d, y + dy, z - 2, WALL);
                set(level, x + d, y + dy, z + 2, WALL);
                set(level, x - 2, y + dy, z + d, WALL);
                set(level, x + 2, y + dy, z + d, WALL);
            }
        }
        // Door opening on the front wall.
        set(level, x, y, z - 2, Blocks.AIR.defaultBlockState());
        set(level, x, y + 1, z - 2, Blocks.AIR.defaultBlockState());
        // Windows.
        BlockState glass = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
        set(level, x - 1, y + 1, z - 2, glass); set(level, x + 1, y + 1, z - 2, glass);
        set(level, x - 2, y + 1, z, glass); set(level, x + 2, y + 1, z, glass);
        // A little interior furniture block and roof ridge.
        set(level, x - 1, y, z + 1, Blocks.BROWN_CARPET.defaultBlockState());
        for (int dx = -2; dx <= 2; dx++) {
            set(level, x + dx, y + 3, z - 2, ROOF);
            set(level, x + dx, y + 3, z + 2, ROOF);
        }
        for (int dz = -1; dz <= 1; dz++) {
            set(level, x - 2, y + 3, z + dz, ROOF);
            set(level, x + 2, y + 3, z + dz, ROOF);
        }
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            set(level, x + dx, y + 4, z + dz, ROOF);
        }
        // Small paved path leading from the door.
        for (int step = 1; step <= 3; step++) {
            set(level, x, y - 1, z - 2 - step, ROAD);
        }
        // A few deliberately sparse block props in the yard.
        if (random.nextBoolean()) set(level, x + 3, y, z + 2, Blocks.FLOWER_POT.defaultBlockState());
    }

    private void buildFountain(WorldGenLevel level, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            if (Math.abs(dx) == 2 || Math.abs(dz) == 2) set(level, x + dx, y - 1, z + dz, ROAD);
        }
        set(level, x, y - 1, z, Blocks.SMOOTH_STONE.defaultBlockState());
        set(level, x, y, z, Blocks.WATER.defaultBlockState());
        set(level, x + 1, y, z, Blocks.WATER.defaultBlockState());
        set(level, x - 1, y, z, Blocks.WATER.defaultBlockState());
        set(level, x, y, z + 1, Blocks.WATER.defaultBlockState());
        set(level, x, y, z - 1, Blocks.WATER.defaultBlockState());
        set(level, x, y + 1, z, Blocks.WATER.defaultBlockState());
        set(level, x + 2, y, z, Blocks.STONE_BRICKS.defaultBlockState());
        set(level, x - 2, y, z, Blocks.STONE_BRICKS.defaultBlockState());
        set(level, x, y, z + 2, Blocks.STONE_BRICKS.defaultBlockState());
        set(level, x, y, z - 2, Blocks.STONE_BRICKS.defaultBlockState());
    }

    private void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 2);
    }
}
