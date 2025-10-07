package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.item.AetherpunkMaceItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public interface AetherpunkItems {
    ArrayList<ItemStack> AETHERPUNK_ITEMS = new ArrayList();
    ItemGroup AETHERPUNK_GROUP = FabricItemGroup.builder().displayName(Text.translatable("itemGroup.aetherpunk.aetherpunk_group")).icon(Aetherpunk::getRecipeKindIcon).build();
    Map<Item, Identifier> ITEMS = new LinkedHashMap();
    Item MACE = createItem("aetherpunk_mace", new AetherpunkMaceItem((new FabricItemSettings()).rarity(Rarity.EPIC).maxCount(1).fireproof()), AetherpunkEnchantments.RELOCITY, AetherpunkEnchantments.OVERLOAD, AetherpunkEnchantments.VOLATILE);
    Item ANCIENT_CORE = createItem("ancient_core", new BlockItem((AetherpunkBlocks.ANCIENT_CORE), new Item.Settings().maxCount(1).fireproof()));
    Item DIAMOND_BOTTLE = createItem("diamond_bottle", new PotionItem(new Item.Settings()));

    static void init() {
        Registry.register(Registries.ITEM_GROUP, Aetherpunk.id("aetherpunk"), AETHERPUNK_GROUP);
        ITEMS.keySet().forEach((item) -> Registry.register(Registries.ITEM, (Identifier)ITEMS.get(item), item));
        Registries.ITEM_GROUP.getKey(AETHERPUNK_GROUP).ifPresent((key) -> AETHERPUNK_ITEMS.forEach((stack) -> ItemGroupEvents.modifyEntriesEvent(key).register((ItemGroupEvents.ModifyEntries)(content) -> content.add(stack))));
    }

    static <T extends Item> T createItem(String name, T item, Enchantment... enchantments) {
        ITEMS.put(item, Aetherpunk.id(name));
        AETHERPUNK_ITEMS.add(item.getDefaultStack());

        for(Enchantment enchantment : enchantments) {
            ItemStack stack = new ItemStack(item);
            stack.addEnchantment(enchantment, enchantment.getMaxLevel());
            AETHERPUNK_ITEMS.add(stack);
        }

        return item;
    }
}