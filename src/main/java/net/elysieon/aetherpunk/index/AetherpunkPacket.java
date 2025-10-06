package net.elysieon.aetherpunk.index;

import net.elysieon.aetherpunk.Aetherpunk;
import net.elysieon.aetherpunk.client.packet.SparkPacket;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AetherpunkPacket {
    public static final Identifier SPARK = new Identifier(Aetherpunk.MOD_ID, "spark");


    public static void init(){
        ClientPlayNetworking.registerGlobalReceiver(SPARK, SparkPacket::receive);}
}