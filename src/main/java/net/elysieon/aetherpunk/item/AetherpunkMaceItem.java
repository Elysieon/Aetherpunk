package net.elysieon.aetherpunk.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.index.AetherpunkDamageTypes;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AetherpunkMaceItem extends Item {
    private static final float ATTACK_DAMAGE = 6F;
    private static final float ATTACK_SPEED = 1.2F;

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public AetherpunkMaceItem(FabricItemSettings settings) {
        super(settings);

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
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) rgbColor = 0xAADA6D;
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

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.fail(stack);
        } else {
            if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) {
                MaceComponent mace = MaceComponent.get(player);
                if (mace.getCharge() == 1) {
                    mace.setCharge(0);
                    player.playSound(AetherpunkSounds.RELOCITY, 0.3f, 1.1f);
                    player.setVelocity(player.getRotationVector().multiply((double) 2.1F, (double) 1.3F, (double) 2.1F));
                    mace.handleParticles();
                    return super.use(world, player, hand);
                }
            }
        }
        return TypedActionResult.fail(stack);
    }

    public void maceHit(ItemStack stack, LivingEntity target, PlayerEntity attacker) {
        Vec3d vel = attacker.getVelocity();
        float damage;
        double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) {
            damage = (float) (horizontalSpeed * 9);
        } else {
            damage = (float) (horizontalSpeed * 11);
        }
        float truedamage = 0;

        if (damage > 0.8) {
            MaceComponent mace = MaceComponent.get(attacker);
            if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) {
                mace.setChargeOverload(350);
            } else {
                mace.setCharge(400);
            }
            for (int i = 0; i < damage; i++) {
                if (!target.isDead()) {
                    truedamage = damage * 3.2f;
                    if (truedamage > 16) {
                        truedamage = 16;
                    }
                    target.damage(target.getDamageSources().create(AetherpunkDamageTypes.OVERLOAD), truedamage);
                }
            }
            attacker.setVelocity
                    (
                            attacker.getVelocity().x * 1.3,
                            0.4 + damage / 25,
                            attacker.getVelocity().z * 1.3
                    );
            attacker.velocityModified = true;
            if (!target.getWorld().isClient) {
                attacker.getWorld().playSound(
                        null,
                        attacker.getBlockPos(),
                        AetherpunkSounds.MACE_IMPACT_1,
                        SoundCategory.PLAYERS,
                        1.5f,
                        AetherpunkUtil.random(1.1f, 1.15f)
                );
            }
            if (!target.getWorld().isClient) {
                attacker.getWorld().playSound(
                        null,
                        attacker.getBlockPos(),
                        AetherpunkSounds.MACE_IMPACT_2,
                        SoundCategory.PLAYERS,
                        2.5f, AetherpunkUtil.random(1.1f, 1.15f)
                );
            }
        }
    }


    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) {
            Vec3d vel = attacker.getVelocity();
            float damage;
            double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
            damage = (float) (horizontalSpeed * 11);
            float truedamage = 0;
            if (damage > 10) {
                if (attacker instanceof PlayerEntity player) {
                    MaceComponent mace = MaceComponent.get(player);
                    if (mace.getChargeOverload() == 1) {
                        mace.setChargeOverload(0);
                        for (int i = 0; i < damage; i++) {
                            if (!target.isDead()) {
                                truedamage = damage * 3.6f;
                                if (truedamage > 23) {
                                    truedamage = 23;
                                }
                                target.damage(target.getDamageSources().create(AetherpunkDamageTypes.OVERLOAD), truedamage);
                            }
                        }
                        attacker.setVelocity
                                (
                                        attacker.getVelocity().x * 2.5,
                                        1,
                                        attacker.getVelocity().z * 2.5
                                );
                        attacker.velocityModified = true;
                        if (!target.getWorld().isClient) {
                            attacker.getWorld().playSound(
                                    null,
                                    attacker.getBlockPos(),
                                    AetherpunkSounds.MACE_IMPACT_3,
                                    SoundCategory.PLAYERS,
                                    1.5f,
                                    0.9f
                            );
                        }
                    } else {
                        maceHit(stack, target, player);
                    }
                }
            } else {
                if (attacker instanceof PlayerEntity player) {
                    maceHit(stack, target, player);
                }

            }
        } else {
            if (attacker instanceof PlayerEntity player) {
                maceHit(stack, target, player);
            }
        }


        return super.postHit(stack, target, attacker);
    }


}