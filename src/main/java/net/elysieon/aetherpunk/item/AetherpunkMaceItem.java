package net.elysieon.aetherpunk.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
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

    // Aetherpunk Mace Logic



}