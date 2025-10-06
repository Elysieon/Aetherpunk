package net.elysieon.aetherpunk.client.packet;

import net.elysieon.aetherpunk.index.AetherpunkParticles;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class SparkPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        double speed = buf.readDouble();

        float r = buf.readFloat();
        float g = buf.readFloat();
        float b = buf.readFloat();
        client.execute(() -> {
                    Random random = handler.getWorld().getRandom();
                    var motionX = random.nextGaussian() / speed;
                    var motionY = random.nextGaussian() / speed;
                    var motionZ = random.nextGaussian() / speed;
                    WorldParticleBuilder.create(AetherpunkParticles.SPARK)
                            .setLifetime(25)
                            .setColorData(
                                    ColorParticleData.create(new Color(r, g, b, 0.75F))
                                            .setEasing(Easing.CIRC_OUT)
                                            .build()
                            )
                            .setScaleData(
                                    GenericParticleData.create(0.24f, 0.12f, 0f).build()
                            )
                            .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((handler.getWorld().getTime() * 0.2f) % 6.28f).setEasing(Easing.SINE_IN).build())
                            .addMotion(motionX, motionY, motionZ)
                            .spawn(handler.getWorld(), x, y, z
                            );
                }
        );
    }
}