package net.elysieon.aetherpunk.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.AetherpunkClient;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class MaceComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final Vec3i AETHER_COLOR = new Vec3i(255, 255, 255);
    public static final Vec3i RELOCITY_COLOR = new Vec3i(142, 211, 133);
    public static final Vec3i OVERLOAD_COLOR = new Vec3i(245, 73, 12);
    public static final Vec3i VOLATILE_COLOR = new Vec3i(206, 48, 48);

    private final PlayerEntity player;
    private final IntOpenHashSet slicedEntities = new IntOpenHashSet();
    private boolean particleActive = false;

    private int frozen = 0;
    private int maceCharge = 400;
    private int maceOverloadCharge = 350;

    public static MaceComponent get(@NotNull PlayerEntity player) {
        return (MaceComponent) Aetherpunk.MACE.get(player);
    }

    public int getChargeTint(ItemStack stack) {
        Vec3i color = AETHER_COLOR;
        float percent = Math.min(this.getCharge(), 1.0F);
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) color = RELOCITY_COLOR;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) color = OVERLOAD_COLOR;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) color = VOLATILE_COLOR;

        percent = Math.max(0.0F, percent);
        int r = (int)(255.0F - percent * (float)(255 - color.getX()));
        int g = (int)(255.0F - percent * (float)(255 - color.getY()));
        int b = (int)(255.0F - percent * (float)(255 - color.getZ()));
        return r << 16 | g << 8 | b;
    }


    public boolean isFrozen() {
        if (this.frozen > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void sparkFrozen() {
        this.frozen = 5;
        this.sync();
    }

    public float getFrozen() {
        return this.frozen;
    }

    public float getCharge() {
        return (float)this.maceCharge / 400;
    }

    public float getChargeOverload() {
        return (float)this.maceOverloadCharge / 350;
    }

    public void setCharge(int Charge) {
        this.maceCharge = Charge;
        this.sync();
    }

    public void setChargeOverload(int Charge) {
        this.maceOverloadCharge = Charge;
        this.sync();
    }



    public MaceComponent(PlayerEntity playerEntity) {
        this.player = playerEntity;
    }

    public TypedActionResult<ItemStack> handleParticles(World world, PlayerEntity player, Hand hand) {
        if (!this.particleActive) {
            this.particleActive = true;
        }

        return TypedActionResult.success(this.player.getStackInHand(hand));
    }


    private void sync() {
        Aetherpunk.MACE.sync(this.player);
    }

    public void clientTick() {

    }


    @Override
    public void tick() {
        ItemStack stack = this.player.getMainHandStack();
        boolean isMace = this.player.isAlive() && !this.player.isDead() && stack.isOf(AetherpunkItems.MACE);
        if (this.frozen == 1) {
            if (!player.getWorld().isClient) {
//                player.getWorld().playSound(
//                        null,
//                        player.getBlockPos(),
//                        ModSounds.MACE_IMPACT_2,
//                        SoundCategory.PLAYERS,
//                        2.5f,
//                        0.9f
//                );
            }
        }
        if (this.frozen > 0) {
            this.frozen = this.frozen - 1;
            this.sync();
        }

        if (this.maceOverloadCharge < 350) {
            this.maceOverloadCharge ++;
        }
        if (this.maceCharge < 400) {
            this.maceCharge ++;
        }
        this.sync();
    }

    public void readFromNbt(NbtCompound tag) {
        this.particleActive = tag.getBoolean("particleActive");
        this.maceCharge = tag.getInt("maceCharge");
        this.maceOverloadCharge = tag.getInt("maceOverloadCharge");
        this.frozen = tag.getInt("frozen");
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("particleActive", this.particleActive);
        tag.putInt("maceCharge", this.maceCharge);
        tag.putInt("maceOverloadCharge", this.maceOverloadCharge);
        tag.putInt("frozen", this.frozen);
    }
}