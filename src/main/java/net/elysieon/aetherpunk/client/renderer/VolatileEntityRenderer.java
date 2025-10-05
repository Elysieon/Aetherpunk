package net.elysieon.aetherpunk.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.entity.VolatileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import team.lodestar.lodestone.helpers.RenderHelper;


public class VolatileEntityRenderer<T extends VolatileEntity> extends EntityRenderer<T> {
    private static final Identifier TEXTURE = Aetherpunk.id("textures/entity/volatile.png");

    public VolatileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        if (MinecraftClient.getInstance().options.debugEnabled) {
            matrices.push();
            RenderUtil.drawBox(matrices, vertexConsumers, entity.getDamageBoxSize(), entity, 1f, 0.5f, 0.25f, 1);
            matrices.pop();
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
