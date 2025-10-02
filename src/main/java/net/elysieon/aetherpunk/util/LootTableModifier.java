package net.elysieon.aetherpunk.util;

import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootTableModifier {

    // Trail Ruin Rare Archaeology Modifier :3
    public static void LootTableModifier() {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
            if(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY.equals(id)) {
                     List<LootPoolEntry> entries = new ArrayList<>(Arrays.asList(original.pools[0].entries));
                     entries.add(ItemEntry.builder(AetherpunkItems.ANCIENT_CORE).build());
                     LootPool.Builder pool = LootPool.builder().with(entries);
                     return LootTable.builder().pool(pool).build();
                 }
            return null;
        });
    }
}
