package net.elysieon.aetherpunk.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.index.AetherpunkDamageTypes;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
        if (target instanceof PlayerEntity targetPlayer) {
            final var targetMaceComponent = MaceComponent.get(targetPlayer);
            targetMaceComponent.sparkFrozen();
        }

        if (!target.getWorld().isClient)
            player.getWorld().playSound(null, player.getBlockPos(), AetherpunkSounds.MACE_IMPACT_3, SoundCategory.PLAYERS, 1.5f, 0.9f);
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

        // Refills Charge depending on style
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) mace.setChargeOverload(350);
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

        return super.postHit(stack, target, attacker);
    }
}