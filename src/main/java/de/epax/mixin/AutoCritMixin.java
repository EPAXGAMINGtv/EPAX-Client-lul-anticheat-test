package de.epax.mixin;

import de.epax.commands.EpaxClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class AutoCritMixin {

    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    public void onInput(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.crosshairTarget == null || EpaxClientState.autocrit == false) return;

        if (client.options.attackKey.isPressed() && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            ClientPlayerEntity player = client.player;
            if (player.isOnGround()) {
                player.jump();
            }
            client.execute(() -> {
                client.interactionManager.attackEntity(player, (( EntityHitResult) client.crosshairTarget).getEntity());
                player.swingHand(Hand.MAIN_HAND);
            });
        }
    }
}
