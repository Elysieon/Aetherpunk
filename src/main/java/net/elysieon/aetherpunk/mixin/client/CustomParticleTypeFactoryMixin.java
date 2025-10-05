package net.elysieon.aetherpunk.mixin.client;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.client.particle.DustPillarParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class CustomParticleTypeFactoryMixin {
    @Shadow
    protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory);

    @Inject(at = @At("HEAD"), method = "registerDefaultFactories")
    private void init(CallbackInfo info) {
        this.registerFactory(Aetherpunk.DUST_PILLAR, new DustPillarParticle.DustPillarFactory());
    }
}