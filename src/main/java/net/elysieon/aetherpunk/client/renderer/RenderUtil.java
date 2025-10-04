package net.elysieon.aetherpunk.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RenderUtil {
    public static void drawBox(
            MatrixStack matrices, VertexConsumerProvider consumers, Box box, Entity entity,
            float r, float g, float b, float a
    ) {
        final var camera = MinecraftClient.getInstance().cameraEntity;
        if (camera == null) return;

        drawCuboidShapeOutline(
                matrices, consumers.getBuffer(RenderLayer.getLines()), VoxelShapes.cuboid(box),
                -entity.getX(), -entity.getY(), -entity.getZ(),
                r, g, b, a
        );
    }

    public static void drawCuboidShapeOutline(
            MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape,
            double offsetX, double offsetY, double offsetZ,
            float red, float green, float blue, float alpha
    ) {
        MatrixStack.Entry entry = matrices.peek();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float) (maxX - minX);
            float l = (float) (maxY - minY);
            float m = (float) (maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            k /= n;
            l /= n;
            m /= n;
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (minX + offsetX), (float) (minY + offsetY),
                    (float) (minZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k, l, m).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (maxX + offsetX), (float) (maxY + offsetY),
                    (float) (maxZ + offsetZ)).color(red, green, blue, alpha).normal(entry.getNormalMatrix(), k, l, m).next();
        });
    }
}