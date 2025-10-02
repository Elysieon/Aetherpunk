package net.elysieon.aetherpunk.index;

import java.util.LinkedHashMap;
import java.util.Map;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface AetherpunkEnchantments {
    Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap();
    Enchantment RELOCITY = createEnchantment("relocity", new RelocityEnchantment());
    Enchantment OVERLOAD = createEnchantment("overload", new OverloadEnchantment());
    Enchantment VOLATILE = createEnchantment("volatile", new VolatileEnchantment());

    static void init() {
        ENCHANTMENTS.keySet().forEach((enchantment) -> Registry.register(Registries.ENCHANTMENT, (Identifier)ENCHANTMENTS.get(enchantment), enchantment));
    }

    static <T extends Enchantment> T createEnchantment(String name, T enchantment) {
        ENCHANTMENTS.put(enchantment, Aetherpunk.id(name));
        return enchantment;
    }
}