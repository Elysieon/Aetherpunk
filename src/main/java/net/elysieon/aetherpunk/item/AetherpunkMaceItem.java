package net.elysieon.aetherpunk.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.entity.VolatileEntity;
import net.elysieon.aetherpunk.index.*;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class AetherpunkMaceItem extends ToolItem {
    private static final float ATTACK_DAMAGE = 6F;
    private static final float ATTACK_SPEED = 1.2F;

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public AetherpunkMaceItem(FabricItemSettings settings) {
        super(AetherpunkToolMaterial.INSTANCE, settings);
        this.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
                .put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                        ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                        ATTACK_DAMAGE, EntityAttributeModifier.Operation.ADDITION
                ))
                .put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
                        ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                        ATTACK_SPEED - 4F, EntityAttributeModifier.Operation.ADDITION
                ))
                .build();
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public Text getName(ItemStack stack) {
        var rgbColor = 0x81DAD0;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) rgbColor = 0x6FF28D;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) rgbColor = 0xFDE37F;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) rgbColor = 0xCF493E;
        return Text.translatable(this.getTranslationKey(stack)).setStyle(Style.EMPTY.withColor(rgbColor));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
        }

    // Aetherpunk Mace Logic

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        MaceComponent mace = MaceComponent.get(player);
        if ((hand == Hand.OFF_HAND) || (!(AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY))) || (!(mace.getCharge() >= 1))) return TypedActionResult.fail(stack);

        mace.setCharge(0);
        player.playSound(AetherpunkSounds.RELOCITY, 0.3f, 1.1f);
        player.setVelocity(player.getRotationVector().multiply((double) 2.4F, (double) 1.3F, (double) 2.4F));
        mace.handleParticles();
        return super.use(world, player, hand);
    }

    public void overloadHit(LivingEntity target, PlayerEntity player, float damage) {
        MaceComponent mace = MaceComponent.get(player);
        mace.setChargeOverload(0);
        damage = Math.min(23, damage * 3.6f); // Higher Multiplier and higher cap
        target.damage(target.getDamageSources().create(AetherpunkDamageTypes.OVERLOAD), damage);
        player.setVelocity(player.getVelocity().x * 2.5, 1, player.getVelocity().z * 2.5);
        player.velocityModified = true;

        mace.sparkFrozen();
        mace.setFrozenstate(1);
        if (target instanceof PlayerEntity targetPlayer) {
            final var targetMaceComponent = MaceComponent.get(targetPlayer);
            targetMaceComponent.sparkFrozen();
            targetMaceComponent.setFrozenstate(1);
        }

        if (!target.getWorld().isClient)
            player.getWorld().playSound(null, player.getBlockPos(), AetherpunkSounds.MACE_IMPACT_3, SoundCategory.PLAYERS, 1.5f, 0.9f);

        // Hit Particles
        if (!player.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) player.getWorld();
            serverWorld.spawnParticles(
                    AetherpunkParticles.SHOCKWAVE,
                    target.getX(), target.getY() + 1, target.getZ(),
                    1,
                    0, 0, 0,
                    0.25
            );
        }

        for (int i = 0; i < 75; i++) {
            for (PlayerEntity loopedplayer : player.getWorld().getPlayers()) {
                if (loopedplayer instanceof ServerPlayerEntity serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, AetherpunkPacket.SPARK, new PacketByteBuf(PacketByteBufs
                            .create()
                            .writeDouble((target.getX()))
                            .writeDouble((target.getY() + 1))
                            .writeDouble((target.getZ()))
                            .writeDouble(2)
                            .writeFloat((0.9F))
                            .writeFloat((0.64F))
                            .writeFloat((0.1F))
                    ));
                }
            }
        }
    }

    public void volatileHit(LivingEntity target, PlayerEntity player, float damage) {
        MaceComponent mace = MaceComponent.get(player);
        mace.setChargeVolatile(0);
        damage = Math.min(20, damage * 3.2f); // Slightly higher cap
        target.damage(target.getDamageSources().create(AetherpunkDamageTypes.VOLATILE), damage);
        player.damage(player.getDamageSources().create(AetherpunkDamageTypes.VOLATILE), damage * 0.75f);

        // Handle Entity
        VolatileEntity volatileEntity = new VolatileEntity(player.getWorld(), player);
        volatileEntity.setVelocity(player.getVelocity().withAxis(Direction.Axis.Y, 0).normalize().multiply(Math.max(0.75, player.getVelocity().length())));
        volatileEntity.velocityModified = true;
        volatileEntity.noClip = true;
        volatileEntity.damage = damage * 0.5f;
        player.getWorld().spawnEntity(volatileEntity);

        // Frozenstate
        mace.sparkFrozen();
        mace.setFrozenstateFromVolatile(2);
        if (target instanceof PlayerEntity targetPlayer) {
            final var targetMaceComponent = MaceComponent.get(targetPlayer);
            targetMaceComponent.sparkFrozen();
            targetMaceComponent.setFrozenstate(2);
        }

        // Push Player
        target.setVelocity(player.getVelocity().x * 1.75f, 1, player.getVelocity().z * 1.75f);
        player.setVelocity(player.getVelocity().x * -1, 1, player.getVelocity().z * -1f);
        player.velocityModified = true;
        target.velocityModified = true;

        // Sound Event
        if (!player.getWorld().isClient) {
            player.getWorld().playSound(null, player.getBlockPos(), AetherpunkSounds.MACE_IMPACT_1, SoundCategory.PLAYERS, 1f, AetherpunkUtil.random(1.1f, 1.15f));
            player.getWorld().playSound(null, player.getBlockPos(), AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 2f, AetherpunkUtil.random(1.1f, 1.15f));
        }

        // Hit Particles
        if (!player.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) player.getWorld();
            serverWorld.spawnParticles(
                    AetherpunkParticles.SHOCKWAVER,
                    target.getX(), target.getY() + 1, target.getZ(),
                    1,
                    0, 0, 0,
                    0.25
            );
        }

        for (int i = 0; i < 50; i++) {
            for (PlayerEntity loopedplayer : player.getWorld().getPlayers()) {
                if (loopedplayer instanceof ServerPlayerEntity serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, AetherpunkPacket.SPARK, new PacketByteBuf(PacketByteBufs
                            .create()
                            .writeDouble((target.getX()))
                            .writeDouble((target.getY() + 1))
                            .writeDouble((target.getZ()))
                            .writeDouble(4)
                            .writeFloat((0.9F))
                            .writeFloat((0.15F))
                            .writeFloat((0.2F))
                    ));
                }
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MaceComponent mace = MaceComponent.get((PlayerEntity) attacker);

        // Checks if the player is going fast enough to activate special maceHit
        Vec3d vel = attacker.getVelocity();
        double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        final var damageMultiplier = AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY) ? 9F : 11F;
        var damage = (float) (horizontalSpeed * damageMultiplier);
        if (!(damage > 0.8)) return super.postHit(stack, target, attacker);

        // Checks if the hit has requirements for overload and returns if so and activates overload hit
        if ((damage > 10) && (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) && mace.getChargeOverload() >= 1) {
            overloadHit(target, (PlayerEntity) attacker, damage);
            return super.postHit(stack, target, attacker);
        }

        // Checks requirements for volatile
        if ((AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) && mace.getChargeVolatile() >= 1) {
            volatileHit(target, (PlayerEntity) attacker, damage);

            if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) {
                attacker.getWorld().playSound(null, attacker.getBlockPos(), AetherpunkSounds.VOLATILE, SoundCategory.PLAYERS, 0.25f, AetherpunkUtil.random(1.1f, 1.15f));
            }

            return super.postHit(stack, target, attacker);
        }


        // Refills Charge depending on style
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) mace.setChargeOverload((int) Math.min((mace.getChargeOverload() * 350) + 175, 350));
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) mace.setChargeVolatile(350);
        mace.setCharge(400);

        // Set Velocity
        attacker.setVelocity(attacker.getVelocity().x * 1.3, 0.4 + damage / 25, attacker.getVelocity().z * 1.3);
        attacker.velocityModified = true;

        // Damages the target
        damage = Math.min(16, damage * 3.2f); // Max damage = 16
        target.damage(target.getDamageSources().create(AetherpunkDamageTypes.SLAM), damage);

        // Sound Event
        if (!target.getWorld().isClient) {
            attacker.getWorld().playSound(null, attacker.getBlockPos(), AetherpunkSounds.MACE_IMPACT_1, SoundCategory.PLAYERS, 1f, AetherpunkUtil.random(1.1f, 1.15f));
            attacker.getWorld().playSound(null, attacker.getBlockPos(), AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 2f, AetherpunkUtil.random(1.1f, 1.15f));
        }

        // Handle Particles
        if (!attacker.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) attacker.getWorld();
            if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) serverWorld.spawnParticles(AetherpunkParticles.SHOCKWAVEG, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0.25);
            else if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) serverWorld.spawnParticles(AetherpunkParticles.SHOCKWAVE, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0.25);
            else if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) serverWorld.spawnParticles(AetherpunkParticles.SHOCKWAVER, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0.25);
            else serverWorld.spawnParticles(AetherpunkParticles.SHOCKWAVEB, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0.25);

        }

        // Spark Color [ 1 = 255 ]
        var r = 0F; var g = 0.9F; var b = 0.9F;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) {r = 0.2F; g = 0.9F; b = 0.2F;}
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) {r = 0.9F; g = 0.64F; b = 0.1F;}
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) {r = 0.9F; g = 0.15F; b = 0.2F;}

        // Spark Particle ✨✨🌟 so skibidi
        for (int i = 0; i < 25; i++) {
            for (PlayerEntity loopedplayer : attacker.getWorld().getPlayers()) {
                if (loopedplayer instanceof ServerPlayerEntity serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, AetherpunkPacket.SPARK, new PacketByteBuf(PacketByteBufs
                            .create()
                            .writeDouble((target.getX()))
                            .writeDouble((target.getY() + 1))
                            .writeDouble((target.getZ()))
                            .writeDouble(8)
                            .writeFloat((r))
                            .writeFloat((g))
                            .writeFloat((b))
                    ));
                }
            }
        }

        return super.postHit(stack, target, attacker);
    }
}