package net.elysieon.aetherpunk.entity;

import net.elysieon.aetherpunk.index.AetherpunkDamageTypes;
import net.elysieon.aetherpunk.index.AetherpunkEntities;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class VolatileEntity extends PersistentProjectileEntity {
    public int ticksUntilRemove = 25;
    public float damage = 0;
    public final List<LivingEntity> hitEntities = new ArrayList<>();

    public VolatileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public VolatileEntity(World world, LivingEntity owner) {
        super(AetherpunkEntities.VOLATILE_ENTITY, owner, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        damage = damage + 0.3f;
        List<LivingEntity> detectedEntities = this.getWorld().getEntitiesByClass(
                LivingEntity.class,
                Box.from(this.getPos()).expand(damage * 0.3),
                entity -> true
        );

        for (var e : detectedEntities) {
            if ((e == this.getOwner())) continue;
            if (hitEntities.contains(e)) continue;
            hitEntities.add(e);
            e.setVelocity(this.getVelocity().x, 1, this.getVelocity().z);
            e.getWorld().playSound(null, e.getBlockPos(), AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 2f, AetherpunkUtil.random(1.1f, 1.15f));
            e.getWorld().playSound(null, e.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, AetherpunkUtil.random(1.1f, 1.15f));
            e.damage(e.getDamageSources().create(AetherpunkDamageTypes.SLAM), damage);
        }

        // Removes itself after abit
        --this.ticksUntilRemove;
        if (this.ticksUntilRemove <= 0) this.discard();
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

    }
}