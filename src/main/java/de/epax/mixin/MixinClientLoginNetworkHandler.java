package de.epax.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.DisconnectionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Inject(method = "onDisconnected", at = @At("HEAD"), cancellable = true)
    private void onDisconnected(DisconnectionInfo info, CallbackInfo ci) {
        if (info.reason().getString().toLowerCase().contains("banned")) {
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(null);
            });
            ci.cancel();
        }
    }
}
