package net.elysieon.aetherpunk.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.AetherpunkClient;
import net.elysieon.aetherpunk.effect.FlashEffect;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
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

import java.awt.*;

public class MaceComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final Vec3i AETHER_COLOR = new Vec3i(255, 255, 255);
    public static final Vec3i RELOCITY_COLOR = new Vec3i(142, 211, 133);
    public static final Vec3i OVERLOAD_COLOR = new Vec3i(109, 255, 30   );
    public static final Vec3i VOLATILE_COLOR = new Vec3i(255, 43, 55);

    private final PlayerEntity player;

    private boolean particleActive = false;

    private int frozen = 0;
    private int maceCharge = 400;
    private int maceOverloadCharge = 350;
    private int previousFrozen = 0;

    public static MaceComponent get(@NotNull PlayerEntity player) {
        return (MaceComponent) Aetherpunk.MACE.get(player);
    }
    public int getChargeTint(ItemStack stack) {
        Vec3i color = AETHER_COLOR;
        float percent = Math.min(this.getCharge(), 1.0F);
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) color = RELOCITY_COLOR;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) color = OVERLOAD_COLOR;
        if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) color = VOLATILE_COLOR;

        // So gay of me
        if (stack.getName().getString().equals("Rainbow")) return Color.getHSBColor((float)(player.getWorld().getTime() / 100d) % 360, 1, 1).getRGB();

        percent = Math.max(0.0F, percent);
        int r = (int)(255.0F - percent * (float)(255 - color.getX()));
        int g = (int)(255.0F - percent * (float)(255 - color.getY()));
        int b = (int)(255.0F - percent * (float)(255 - color.getZ()));
        return r << 16 | g << 8 | b;
    }


    // FlashEffect :3
    // Grazie was my pookie </3
    @SuppressWarnings("GrazieInspection")
    public float getFrost() {

        return this.frozen;
    }

    public void sparkFrozen() {
        this.frozen = 5;
        this.sync();
    }
    public boolean isFrozen() {
        return this.frozen > 0;
    }

    private void tickFrozenState() {
        // On the last tick the player is frozen for.
        if (this.frozen == 1 && !this.player.getWorld().isClient) {
            this.player.getWorld().playSound(
                    null, this.player.getBlockPos(),
                        AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 1.25f, 0.9f
            );
        }

        if (this.frozen > 0) {
            this.frozen -= 1;
            this.sync();
        }

        // Basically when we got frozen.
        if (this.player.getWorld().isClient && this.frozen != 0 && this.previousFrozen == 0) {
            FlashEffect.LAST_PARRYING_TIME = player.getWorld().getTime();
        }

        this.previousFrozen = this.frozen;
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

    public void handleParticles() {
        if (!this.particleActive) {
            this.particleActive = true;
        }
    }

    public boolean isParticleActive() {
        return this.particleActive;
    }


    private void sync() {
        Aetherpunk.MACE.sync(this.player);
    }



    @Override
    public void tick() {
        if (this.player.isOnGround() && (this.particleActive)) this.particleActive = false;
        this.tickFrozenState();

        if (this.maceOverloadCharge < 350) this.maceOverloadCharge ++;
        if (this.maceCharge < 400) this.maceCharge ++;

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