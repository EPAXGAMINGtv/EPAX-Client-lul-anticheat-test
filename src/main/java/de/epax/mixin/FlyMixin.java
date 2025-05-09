package de.epax.mixin;

import de.epax.commands.EpaxClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class FlyMixin {

    private boolean simulateGround = false;
    private long lastGroundUpdate = System.nanoTime();
    private double flySpeed = 0.055;
    private double acceleration = 0.02;
    private double deceleration = 0.98;

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    public void tickMovement(CallbackInfo ci) {
        if (EpaxClientState.fly) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            MinecraftClient client = MinecraftClient.getInstance();

            Vec3d motion = calculateMotion(client, player);

            applyMotion(player, motion.x, motion.y, motion.z);
            simulateGroundState(player);
        }
    }

    private Vec3d calculateMotion(MinecraftClient client, ClientPlayerEntity player) {
        Vec3d forward = player.getRotationVec(1.0F).normalize(); // Get forward direction based on player rotation
        Vec3d up = new Vec3d(0, 1, 0); // World up vector
        Vec3d right = forward.crossProduct(up).normalize(); // Right vector based on current forward direction

        double motionX = 0;
        double motionY = 0;
        double motionZ = 0;

        // Vertical movement
        if (client.options.jumpKey.isPressed()) {
            motionY += flySpeed;
        }
        if (client.options.sneakKey.isPressed()) {
            motionY -= flySpeed;
        }

        // Forward / Backward movement
        if (client.options.forwardKey.isPressed()) {
            motionX += forward.x * flySpeed;
            motionZ += forward.z * flySpeed;
        }
        if (client.options.backKey.isPressed()) {
            motionX -= forward.x * flySpeed;
            motionZ -= forward.z * flySpeed;
        }

        // Left / Right (strafing) movement
        if (client.options.leftKey.isPressed()) {
            motionX -= right.x * flySpeed;
            motionZ -= right.z * flySpeed;
        }
        if (client.options.rightKey.isPressed()) {
            motionX += right.x * flySpeed;
            motionZ += right.z * flySpeed;
        }

        // Apply acceleration and deceleration for smoother motion
        motionX = motionX * deceleration + acceleration;
        motionY = motionY * deceleration + acceleration;
        motionZ = motionZ * deceleration + acceleration;

        return new Vec3d(motionX, motionY, motionZ);
    }

    private void applyMotion(ClientPlayerEntity player, double motionX, double motionY, double motionZ) {
        // Add slight random jitter for realism
        double jitterX = (Math.random() - 0.5) * 0.001;
        double jitterY = (Math.random() - 0.5) * 0.001;
        double jitterZ = (Math.random() - 0.5) * 0.001;

        // Apply velocity to the player
        player.setVelocity(motionX + jitterX, motionY + jitterY, motionZ + jitterZ);

        // Update player position
        player.updatePosition(player.getX() + motionX + jitterX, player.getY() + motionY + jitterY, player.getZ() + motionZ + jitterZ);
    }

    private void simulateGroundState(ClientPlayerEntity player) {
        World world = player.getWorld();
        BlockPos blockBelow = new BlockPos((int) player.getX(), (int) (player.getY() - 1.0), (int) player.getZ());

        if (simulateGround || world.getBlockState(blockBelow).isOpaque()) {
            player.setOnGround(true);
            simulateGround = false;
        } else if (System.nanoTime() - lastGroundUpdate > 50_000_000) {
            simulateGround = true;
            lastGroundUpdate = System.nanoTime();
        }
    }
}