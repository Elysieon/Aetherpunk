package net.elysieon.aetherpunk;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.index.AetherpunkBlocks;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.elysieon.aetherpunk.util.LootTableModifier;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aetherpunk implements ModInitializer, EntityComponentInitializer {
	public static final String MOD_ID = "aetherpunk";
    public static Identifier id(String name) {return new Identifier("aetherpunk", name);}

    public static ItemStack getRecipeKindIcon() {
        return AetherpunkItems.MACE.getDefaultStack();
    }

	@Override
	public void onInitialize() {
        AetherpunkItems.init();
        AetherpunkEnchantments.init();
        AetherpunkBlocks.init();

        LootTableModifier.LootTableModifier();
	}

    // Components
    public static final ComponentKey<MaceComponent> MACE = ComponentRegistry.getOrCreate(id("mace"), MaceComponent.class);

    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, MACE).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(MaceComponent::new);
    }

    // Ancient Core Box Math (Rose Made it)
    public static VoxelShape box(
            int minX,
            int minY,
            int minZ,
            int sizeX,
            int sizeY,
            int sizeZ
    ) {
        return VoxelShapes.cuboid(
                minX / 16d,
                minY / 16d,
                minZ / 16d,
                minX / 16d + sizeX / 16d,
                minY / 16d + sizeY / 16d,
                minZ / 16d + sizeZ / 16d
        );
    }
}