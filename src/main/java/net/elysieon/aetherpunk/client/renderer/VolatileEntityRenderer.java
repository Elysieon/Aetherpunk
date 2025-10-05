package net.elysieon.aetherpunk.client.renderer;

import net.elysieon.aetherpunk.entity.VolatileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;


public class VolatileEntityRenderer<T extends VolatileEntity> extends EntityRenderer<T> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/entity/projectiles/arrow.png");

    public VolatileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        if (MinecraftClient.getInstance().options.debugEnabled) {
            matrices.push();
            RenderUtil.drawBox(matrices, vertexConsumers, Box.from(entity.getPos()).expand(entity.damage), entity, 1f, 0.5f, 0.25f, 1);
            matrices.pop();
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }
}
