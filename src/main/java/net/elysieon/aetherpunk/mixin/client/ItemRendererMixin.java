package net.elysieon.aetherpunk.mixin.client;

import net.elysieon.aetherpunk.AetherpunkClient;
import net.elysieon.aetherpunk.index.AetherpunkItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

    @Shadow
    public abstract void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);


    public BakedModel Aetherpunk$useModel(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(AetherpunkItems.MACE) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND) {
            return ((ItemRendererAccessor) this).getModelsA().getModelManager().getModel(new ModelIdentifier("aetherpunk", "aetherpunk_mace_big", "inventory"));
        }

        if (stack.isOf(AetherpunkItems.MACE) && renderMode == ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).getModelsA().getModelManager().getModel(new ModelIdentifier("aetherpunk", "aetherpunk_mace", "inventory"));
        }
        return null;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"),
            cancellable = true
    )
    private void Aetherpunk$renderItem$renderCustomModel(
            LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            World world, int light, int overlay, int seed, CallbackInfo callbackInfo
    ) {
        var bakedModel = Aetherpunk$useModel(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay);
        if (bakedModel == null) return;
        renderItem(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
        callbackInfo.cancel();
    }


    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BakedModel Aetherpunk$renderItem$useCustomModel(
            BakedModel value,
            ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay,
            BakedModel model
    ) {
        var bakedModel = Aetherpunk$useModel(null, stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay);
        return bakedModel == null ? model : bakedModel;
    }

    @Inject(
            method = {"renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"
            )}
    )
    private void aetherpunk$storeEntity(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        if ((item.isOf(AetherpunkItems.MACE)) && entity instanceof PlayerEntity player) {
            AetherpunkClient.renderingPlayer = player;
        }
    }
}