package net.elysieon.aetherpunk.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AetherpunkUtil {
    // Player has enchantment
    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        if (EnchantmentHelper.getLevel(enchantment, stack) > 0) return true;
        return false;
    }

    // Random Number
    public static float random(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

}
