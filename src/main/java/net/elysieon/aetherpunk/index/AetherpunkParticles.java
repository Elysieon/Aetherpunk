package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AetherpunkParticles {
    private static DefaultParticleType of(String name) {
        var particle = FabricParticleTypes.simple();
        Registry.register(Registries.PARTICLE_TYPE, Aetherpunk.id(name), particle);
        return particle;
    }

    public static void init() {
    }
}

