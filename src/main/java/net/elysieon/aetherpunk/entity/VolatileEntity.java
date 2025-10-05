package net.elysieon.aetherpunk.entity;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.index.AetherpunkDamageTypes;
import net.elysieon.aetherpunk.index.AetherpunkEntities;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
                getDamageBoxSize(),
                entity -> true
        );

        for (var detectedEntity : detectedEntities) {
            if ((detectedEntity == this.getOwner())) continue;

            final var entityPos = detectedEntity.getPos();
            final var particlePos = this.findGround(entityPos);
            final var particleState = this.getWorld().getBlockState(particlePos);
            if (!particleState.isAir()) {
                this.getWorld().addBlockBreakParticles(particlePos, particleState);

                if (this.getWorld() instanceof ClientWorld clientWorld) {
                    spawnSmashAttackParticles(
                            clientWorld,
                            particlePos,
                            15
                    );

                    if (clientWorld.getRandom().nextFloat() < 0.25) {
                        clientWorld.addParticle(
                                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                particlePos.getX(),
                                particlePos.getY() + MathHelper.nextBetween(clientWorld.getRandom(), 1, 1.1F),
                                particlePos.getZ(),
                                MathHelper.nextBetween(clientWorld.getRandom(), -0.1F, 0.1F),
                                MathHelper.nextBetween(clientWorld.getRandom(), 0.05F, 0.08F),
                                MathHelper.nextBetween(clientWorld.getRandom(), -0.1F, 0.1F)
                        );
                    }
                }
            }

            if (hitEntities.contains(detectedEntity)) continue;
            hitEntities.add(detectedEntity);
            detectedEntity.setVelocity(this.getVelocity().x, 1, this.getVelocity().z);
            detectedEntity.getWorld().playSound(null, detectedEntity.getBlockPos(), AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 2f, AetherpunkUtil.random(1.1f, 1.15f));
            detectedEntity.damage(detectedEntity.getDamageSources().create(AetherpunkDamageTypes.VOLATILE_RANGE), damage);

            this.getWorld().addImportantParticle(ParticleTypes.FLASH, true, detectedEntity.getX(), detectedEntity.getY(), detectedEntity.getZ(), 0, 0, 0);
        }

        // Removes itself after abit
        --this.ticksUntilRemove;
        if (this.ticksUntilRemove <= 0) this.discard();
    }

    public static void spawnSmashAttackParticles(ClientWorld world, BlockPos pos, int count) {
        Vec3d vec3d = pos.toCenterPos().add(0.0, 0.5, 0.0);

        BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(Aetherpunk.DUST_PILLAR, world.getBlockState(pos));

        for (int i = 0; (float) i < (float) count / 3.0F; i++) {
            double d = vec3d.x + world.getRandom().nextGaussian() / 2.0;
            double e = vec3d.y;
            double f = vec3d.z + world.getRandom().nextGaussian() / 2.0;
            double g = world.getRandom().nextGaussian() * 0.2F;
            double h = world.getRandom().nextGaussian() * 0.2F;
            double j = world.getRandom().nextGaussian() * 0.2F;
            world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
        }

        for (int i = 0; (float) i < (float) count / 1.5F; i++) {
            double d = vec3d.x + 3.5 * Math.cos(i) + world.getRandom().nextGaussian() / 2.0;
            double e = vec3d.y;
            double f = vec3d.z + 3.5 * Math.sin(i) + world.getRandom().nextGaussian() / 2.0;
            double g = world.getRandom().nextGaussian() * 0.05F;
            double h = world.getRandom().nextGaussian() * 0.05F;
            double j = world.getRandom().nextGaussian() * 0.05F;
            world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
        }
    }

    private BlockPos findGround(Vec3d start) {
        var current = start;
        var tokens = 300;
        while (this.getWorld().getBlockState(BlockPos.ofFloored(current)).isAir() && tokens > 0) {
            tokens--;
            current = current.subtract(0, 1, 0);
        }

        return BlockPos.ofFloored(current);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

    }

    public Box getDamageBoxSize() {
        return Box.from(this.getPos()).expand(damage * 0.3);
    }
}