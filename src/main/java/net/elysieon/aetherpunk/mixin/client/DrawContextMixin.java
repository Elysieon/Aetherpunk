package net.elysieon.aetherpunk.mixin.client;

import net.elysieon.aetherpunk.components.MaceComponent;
import net.elysieon.aetherpunk.index.AetherpunkEnchantments;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.elysieon.aetherpunk.util.AetherpunkUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow
    public abstract void fill(RenderLayer var1, int var2, int var3, int var4, int var5, int var6);

    private int interpolateColorHSB(int colorStart, int colorEnd, float t) {
        float[] hsb1 = Color.RGBtoHSB((colorStart >> 16) & 0xFF, (colorStart >> 8) & 0xFF, colorStart & 0xFF, null);
        float[] hsb2 = Color.RGBtoHSB((colorEnd >> 16) & 0xFF, (colorEnd >> 8) & 0xFF, colorEnd & 0xFF, null);
        float hueDiff = hsb2[0] - hsb1[0];
        if (Math.abs(hueDiff) > 0.5f) {
            if (hsb1[0] > hsb2[0]) hsb2[0] += 1f;
            else hsb1[0] += 1f;
        }
        float h = (hsb1[0] + t * (hsb2[0] - hsb1[0])) % 1f;
        float s = hsb1[1] + t * (hsb2[1] - hsb1[1]);
        float b = hsb1[2] + t * (hsb2[2] - hsb1[2]);

        return Color.HSBtoRGB(h, s, b);
    }


    @Inject(
            method = {"drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"},
            at = {@At("HEAD")}
    )
    private void aetherpunk$MaceCharge(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null) {
            if (stack.isOf(AetherpunkItems.MACE)) {
                MaceComponent mace = MaceComponent.get(MinecraftClient.getInstance().player);
                var rgb = 0x81DAD0;
                var charge = mace.getCharge();

                if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.RELOCITY)) rgb = 0x6FF28D;
                if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) rgb = 0xFDE37F;
                if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.VOLATILE)) rgb = 0xD18585;

                if (AetherpunkUtil.hasEnchantment(stack, AetherpunkEnchantments.OVERLOAD)) charge = mace.getChargeOverload();

                this.fill(RenderLayer.getGuiOverlay(), x + 2, y + 13, x + 15, y + 15, -16777216);
                float clampedCharge = Math.min(charge, 1.0F);
                int width = Math.round(clampedCharge * 13.0F);
                this.fill(RenderLayer.getGuiOverlay(), x + 2, y + 13, x + 2 + width, y + 14, rgb | 0xFF000000);
                if (charge > 1.0F) {
                    float overflowCharge = Math.min(charge - 1.0F, 1.0F);
                    int overflowColor = interpolateColorHSB(rgb, rgb, overflowCharge);
                    int overflowWidth = Math.round(overflowCharge * 13.0F);
                    this.fill(RenderLayer.getGuiOverlay(), x + 2, y + 13, x + 2 + overflowWidth, y + 14, overflowColor | 0xFF000000);
                }
            }
        }
    }
}