package net.elysieon.aetherpunk.mixin.client;

import net.elysieon.aetherpunk.components.MaceComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "tickMovement", cancellable = true)
    private void ChasmChambers$CancelMovement(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null) {
            MaceComponent mace = MaceComponent.get(MinecraftClient.getInstance().player);
            if (mace.isFrozen()) {
                ci.cancel();
            }
        }
    }
}