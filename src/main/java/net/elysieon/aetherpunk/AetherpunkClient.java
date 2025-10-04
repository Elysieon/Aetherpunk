package net.elysieon.aetherpunk;

import net.elysieon.aetherpunk.client.renderer.VolatileEntityRenderer;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.effect.FlashEffect;
import net.elysieon.aetherpunk.effect.RedFlashEffect;
import net.elysieon.aetherpunk.index.AetherpunkEntities;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;

public class AetherpunkClient implements ClientModInitializer {
    public static PlayerEntity renderingPlayer = null;

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register(new FlashEffect());
        HudRenderCallback.EVENT.register(new RedFlashEffect());
        EntityRendererRegistry.register(AetherpunkEntities.VOLATILE_ENTITY, VolatileEntityRenderer::new);

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
