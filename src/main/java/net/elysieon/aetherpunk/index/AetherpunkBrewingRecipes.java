package net.elysieon.aetherpunk.index;

import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;

public interface AetherpunkBrewingRecipes {
    public static void init() {
        BrewingRecipeRegistry.registerItemRecipe(
                Items.POTION,
                Items.DIAMOND_BLOCK,
                AetherpunkItems.DIAMOND_BOTTLE
        );
    }
}
