package net.elysieon.aetherpunk.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.mixin_impl.GameRendererMixinImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Environment(EnvType.CLIENT)
    @ModifyReturnValue(at = @At("RETURN"), method = "getFov")
    private double aetherpunk$Velocity(double original, Camera camera, float tickProgress, boolean changingFov) {
        final var localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer != null) original += GameRendererMixinImpl.getFovIncrease(localPlayer, tickProgress);
        return original;
    }
}