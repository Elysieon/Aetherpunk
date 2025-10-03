package net.elysieon.aetherpunk.mixin_impl;

import net.elysieon.aetherpunk.components.MaceComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class GameRendererMixinImpl {
    private static double previousFovIncrease;

    public static float getFovIncrease(PlayerEntity player, float tickDelta) {
        MaceComponent mace = MaceComponent.get(player);
        if (!(mace.isParticleActive())) return 0f;
        final var fovIncrease = Math.max(0, (player.getVelocity().length() - 0.3)) * 3d;
        final var effectiveFovIncrease = MathHelper.lerp(tickDelta, previousFovIncrease, fovIncrease);
        previousFovIncrease = effectiveFovIncrease;

        return (float) effectiveFovIncrease;
    }
}