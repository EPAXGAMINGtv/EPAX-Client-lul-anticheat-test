package de.epax.mixin;

import de.epax.commands.EpaxClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class FlyMixin {

    @Inject(method = "tickMovement",at =@At("HEAD"),cancellable = true)
    public void tickMovement(CallbackInfo ci) {
        if (EpaxClientState.fly==true){
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            double flySpeed = 0.05;
            double motionY = 0;
            if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
                motionY += flySpeed;
            }
            if (MinecraftClient.getInstance().options.sneakKey.isPressed()) {
                motionY -= flySpeed;
            }
            Vec3d velocity = player.getRotationVec(1.0F).normalize().multiply(0.2);
            double motionX = 0;
            double motionZ = 0;
            if (MinecraftClient.getInstance().options.forwardKey.isPressed()) {
                motionX += velocity.x;
                motionZ += velocity.z;
            }
            player.setVelocity(motionX, motionY, motionZ);
            player.setPosition(player.getX() + motionX, player.getY() + motionY, player.getZ() + motionZ);
            player.setOnGround(true);

        }else {

        }
    }
}