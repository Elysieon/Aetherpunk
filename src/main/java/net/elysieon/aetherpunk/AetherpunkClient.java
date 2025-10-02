package net.elysieon.aetherpunk;

import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;

public class AetherpunkClient implements ClientModInitializer {
    public static PlayerEntity renderingPlayer = null;

    @Override
    public void onInitializeClient() {

        ColorProviderRegistry.ITEM.register((ItemColorProvider)(stack, tintIndex) -> {
            if (tintIndex == 0 && renderingPlayer != null) {
                MaceComponent component = MaceComponent.get(renderingPlayer);
                return component.getChargeTint(stack);
            } else {
                return 16777215;
            }
        }, new ItemConvertible[]{AetherpunkItems.MACE});

    }
}
