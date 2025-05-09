package de.epax.mixin;

import de.epax.commands.EpaxClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatEntity.class)
public abstract class BoatFlyMixin {

    private final double flySpeed = 2.2;
    private final double ascendSpeed = 0.3;
    private final double descendSpeed = -0.3;
    private final double smoothingFactor = 0.98;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onBoatTick(CallbackInfo ci) {
        AbstractBoatEntity boat = (AbstractBoatEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player != null && EpaxClientState.boatFly && boat.hasPassenger(player)) {
            Vec3d velocity = boat.getVelocity();
            double motionY = velocity.y;
            double motionX = 0.0;
            double motionZ = 0.0;
            float yaw = player.getYaw();
            float radians = (float) Math.toRadians(yaw);
            if (client.options.forwardKey.isPressed()) {
                motionX += -Math.sin(radians) * flySpeed;
                motionZ += Math.cos(radians) * flySpeed;
            }
            if (client.options.backKey.isPressed()) {
                motionX += Math.sin(radians) * flySpeed;
                motionZ += -Math.cos(radians) * flySpeed;
            }
            if (client.options.leftKey.isPressed()) {
                motionX += Math.cos(radians) * flySpeed;
                motionZ += Math.sin(radians) * flySpeed;
            }
            if (client.options.rightKey.isPressed()) {
                motionX += -Math.cos(radians) * flySpeed;
                motionZ += -Math.sin(radians) * flySpeed;
            }
            if (client.options.jumpKey.isPressed()) {
                motionY = ascendSpeed;
            } else if (client.options.dropKey.isPressed()) {
                motionY = descendSpeed;
            } else {
                motionY *= smoothingFactor;
            }
            boat.setVelocity(motionX, motionY, motionZ);
            boat.setPosition(boat.getX() + motionX, boat.getY() + motionY, boat.getZ() + motionZ);
            ci.cancel();
        }
    }
}