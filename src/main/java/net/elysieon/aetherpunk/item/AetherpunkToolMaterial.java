package net.elysieon.aetherpunk.item;

import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class AetherpunkToolMaterial implements ToolMaterial {
    public static final ToolMaterial INSTANCE = new AetherpunkToolMaterial();

    public int getDurability() {
        return 0;
    }

    public float getMiningSpeedMultiplier() {
        return 10.5F;
    }

    public float getAttackDamage() {
        return 0.0F;
    }

    public int getMiningLevel() {
        return 4;
    }

    public int getEnchantability() {
        return 36;
    }

    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(new ItemConvertible[]{AetherpunkItems.DIAMOND_BOTTLE});
    }
}
