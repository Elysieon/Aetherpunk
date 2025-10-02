package net.elysieon.aetherpunk.util;

import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class AetherpunkUtil {

    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        if (EnchantmentHelper.getLevel(enchantment, stack) > 0) return true;
        return false;
    }
}
