package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.entity.VolatileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface AetherpunkEntities {
    Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap();
    EntityType<VolatileEntity> VOLATILE_ENTITY = createEntity("volatile_entity",
            FabricEntityTypeBuilder.<VolatileEntity>create(SpawnGroup.MISC, VolatileEntity::new)
                    .disableSaving()
                    .dimensions(EntityDimensions.changing(0.25F, 0.25F))
                    .build());

    private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
        ENTITIES.put(entity, new Identifier("aetherpunk", name));
        return entity;
    }

    static void init() {
        ENTITIES.keySet().forEach((entityType) -> Registry.register(Registries.ENTITY_TYPE, (Identifier)ENTITIES.get(entityType), entityType));
    }
}