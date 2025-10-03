package net.elysieon.aetherpunk.effect;

import net.elysieon.aetherpunk.components.MaceComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;

public class FlashEffect implements HudRenderCallback {
    /**
     * Added ticks to the flash duration.
     * This means the player will be full flashed for {BUFFER} more ticks.
     * This does not expand the time it takes to fade out, just extends the full blind duration.
     */
    public static final int BUFFER = 3;
    public static long LAST_PARRYING_TIME;

    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        assert MinecraftClient.getInstance().player != null;
        MaceComponent component = MaceComponent.get(MinecraftClient.getInstance().player);
        if (component.getFrost() > 0) {
            var client = MinecraftClient.getInstance();
            var world = client.world;
            if (world == null) return;

            var difference = world.getTime() - (LAST_PARRYING_TIME + BUFFER);
            var alpha = 150 - Math.max(0, Math.min(150, (int) (((double) difference / 5d) * 150)));
            var colour = ColorHelper.Argb.getArgb(alpha, 255, 255, 255);
            drawContext.fill(0, 0, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight(), colour);
        }
    }
}