package net.elysieon.aetherpunk.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static net.elysieon.aetherpunk.Aetherpunk.box;

public class AncientCoreBlock extends Block {
    public AncientCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(
            BlockState state,
            BlockView world,
            BlockPos pos,
            ShapeContext context
    ) {
        return box(
                4, 0, 4,
                8, 8, 8
        );
    }
}