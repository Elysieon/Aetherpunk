package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.blocks.AncientCoreBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AetherpunkBlocks {
    public static final Block ANCIENT_CORE = of(
            "ancient_core",
            new AncientCoreBlock(
                    AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)
                            .nonOpaque()
            )
    );

    private static <T extends Block> T of(String path, T block) {
        return Registry.register(
                Registries.BLOCK, Aetherpunk.id(path),
                block
        );
    }

    public static void init() {
    }
}
