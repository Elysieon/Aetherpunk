package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class AetherpunkSounds {
    public static final SoundEvent MACE_IMPACT_1 = registerSoundEvent("mace_impact_1");
    public static final SoundEvent MACE_IMPACT_2 = registerSoundEvent("mace_impact_2");
    public static final SoundEvent MACE_IMPACT_3 = registerSoundEvent("mace_impact_3");
    public static final SoundEvent RELOCITY = registerSoundEvent("relocity");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(Aetherpunk.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void init() {
    }

}
