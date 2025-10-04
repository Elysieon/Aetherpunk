package net.elysieon.aetherpunk.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.effect.FlashEffect;
import net.elysieon.aetherpunk.effect.RedFlashEffect;
import net.elysieon.aetherpunk.index.AetherpunkSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MaceComponent implements AutoSyncedComponent, CommonTickingComponent {
    public static final Vec3i AETHER_COLOR = new Vec3i(195, 241, 231);
    public static final Vec3i RELOCITY_COLOR = new Vec3i(142, 211, 133);
    public static final Vec3i OVERLOAD_COLOR = new Vec3i(247, 192, 39);
    public static final Vec3i VOLATILE_COLOR = new Vec3i(165, 41, 53);

    private final PlayerEntity player;

    private boolean particleActive = false;

    private int frozen = 0;
    private int frozenstate = 0;
    private int maceCharge = 400;
    private int maceOverloadCharge = 350;
    private int maceVolatileCharge = 350;
    private int previousFrozen = 0;

    public static MaceComponent get(@NotNull PlayerEntity player) {
        return (MaceComponent) Aetherpunk.MACE.get(player);
    }
    public int getChargeTint(ItemStack stack) {
        // Rainbow UwU
        if (stack.getName().getString().equals("Gaytherpunk")) return Color.getHSBColor((float)(player.getWorld().getTime() / 100d) % 360, 1, 1).getRGB();
        return 16777215;
    }


    // FlashEffect :3
    // Grazie was my pookie </3
    @SuppressWarnings("GrazieInspection")
    public float getFrost() {

        return this.frozen;
    }

    public float getFrozenstate() {
        return this.frozenstate;
    }

    public void sparkFrozen() {
        this.frozen = 5;
        this.sync();
    }

    public void setFrozenstate(int fishystateuwu) {
        this.frozenstate = fishystateuwu;
        this.sync();
    }
    public boolean isFrozen() {
        return this.frozen > 0;
    }

    private void tickFrozenState() {
        // On the last tick the player is frozen for.
        if (this.frozen == 1 && !this.player.getWorld().isClient && this.frozenstate == 1)  {
            this.player.getWorld().playSound(
                    null, this.player.getBlockPos(),
                        AetherpunkSounds.MACE_IMPACT_2, SoundCategory.PLAYERS, 1.25f, 0.9f
            );
        }

        if (this.frozen == 1 && !this.player.getWorld().isClient && this.frozenstate == 2)  {
            this.player.getWorld().playSound(
                    null, this.player.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.25f, 1.25f
            );
        }

        if (this.frozen > 0) {
            this.frozen -= 1;
            this.sync();
        }

        // REMOVE THE FUCKING STATE OF AMERICA AHAHA
        if (this.frozen <= 0) this.frozenstate = 0;

        // Activate flashy boi
        if (this.player.getWorld().isClient && this.frozen != 0 && this.previousFrozen == 0 && this.frozenstate == 1) FlashEffect.LAST_PARRYING_TIME = player.getWorld().getTime();
        if (this.player.getWorld().isClient && this.frozen != 0 && this.previousFrozen == 0 && this.frozenstate == 2) RedFlashEffect.LAST_PARRYING_TIME = player.getWorld().getTime();




        this.previousFrozen = this.frozen;
    }


    public float getCharge() {
        return (float)this.maceCharge / 400;
    }

    public float getChargeOverload() {
        return (float)this.maceOverloadCharge / 350;
    }

    public float getChargeVolatile() {
        return (float)this.maceVolatileCharge / 350;
    }

    public void setCharge(int Charge) {
        this.maceCharge = Charge;
        this.sync();
    }

    public void setChargeOverload(int Charge) {
        this.maceOverloadCharge = Charge;
        this.sync();
    }

    public void setChargeVolatile(int Charge) {
        this.maceVolatileCharge = Charge;
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


    public void handleTrailParticles() {
        if (this.player.isOnGround()) this.particleActive = false;

        //

    }

    @Override
    public void tick() {
        if (this.particleActive) handleTrailParticles();
        this.tickFrozenState();

        if (this.maceVolatileCharge < 350) this.maceVolatileCharge ++;
        if (this.maceOverloadCharge < 350) this.maceOverloadCharge ++;
        if (this.maceCharge < 400) this.maceCharge ++;

        this.sync();
    }

    public void readFromNbt(NbtCompound tag) {
        this.particleActive = tag.getBoolean("particleActive");
        this.maceCharge = tag.getInt("maceCharge");
        this.maceVolatileCharge = tag.getInt("maceVolatileCharge");
        this.maceOverloadCharge = tag.getInt("maceOverloadCharge");
        this.frozen = tag.getInt("frozen");
        this.frozenstate = tag.getInt("frozenstate");
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("particleActive", this.particleActive);
        tag.putInt("maceCharge", this.maceCharge);
        tag.putInt("maceVolatileCharge", this.maceVolatileCharge);
        tag.putInt("maceOverloadCharge", this.maceOverloadCharge);
        tag.putInt("frozen", this.frozen);
        tag.putInt("frozenstate", this.frozenstate);
    }
}