package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.client.particle.ShockwaveParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;
import java.util.LinkedHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public interface AetherpunkParticles {
    Map<ParticleType<?>, Identifier> PARTICLES = new LinkedHashMap();
    LodestoneWorldParticleType SPARK = register("spark", new LodestoneWorldParticleType());
    DefaultParticleType SHOCKWAVE = (DefaultParticleType)create("shockwave", FabricParticleTypes.simple(true));
    DefaultParticleType SHOCKWAVER = (DefaultParticleType)create("shockwaver", FabricParticleTypes.simple(true));
    DefaultParticleType SHOCKWAVEG = (DefaultParticleType)create("shockwaveg", FabricParticleTypes.simple(true));
    DefaultParticleType SHOCKWAVEB = (DefaultParticleType)create("shockwaveb", FabricParticleTypes.simple(true));

    static void init() {
        PARTICLES.keySet().forEach((particle) -> Registry.register(Registries.PARTICLE_TYPE, (Identifier)PARTICLES.get(particle), particle));
    }

    private static LodestoneWorldParticleType register(String name, LodestoneWorldParticleType type) {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier("aetherpunk", name), type);
    }

    private static <T extends ParticleType<?>> T create(String name, T particle) {
        PARTICLES.put(particle, Aetherpunk.id(name));
        return particle;
    }


    static void registerFactories() {
        ParticleFactoryRegistry.getInstance().register(SPARK, LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVER, ShockwaveParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVEG, ShockwaveParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVEB, ShockwaveParticle.Factory::new);

    }
}
