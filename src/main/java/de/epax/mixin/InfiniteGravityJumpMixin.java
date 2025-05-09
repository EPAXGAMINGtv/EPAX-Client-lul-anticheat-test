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
public abstract class InfiniteGravityJumpMixin {

    private boolean isGroundSpoofed = false;
    private int jumpCount = 0;
    private long lastGroundUpdate = System.nanoTime();
    private final double jumpBoost = 0.42; // Standard-Sprungkraft
    private final double gravityEffect = -0.08; // Schwerkraft
    private final double groundSpoofInterval = 50_000_000; // 50ms in Nanosekunden

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void onTickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        if (EpaxClientState.doublejump) {
            if (player.isOnGround()) {
                jumpCount = 0;
                isGroundSpoofed = false;
            } else if (MinecraftClient.getInstance().options.jumpKey.isPressed() && jumpCount < 100) { // Unbegrenzte Sprünge mit Limit für Sicherheit
                performJump(player);
                jumpCount++;
            } else if (isGroundSpoofed) {
                simulateGroundState(player);
            }
        }
    }

    private void performJump(ClientPlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        double newVelocityY = jumpBoost + applyGravity(velocity.y);
        player.setVelocity(velocity.x, newVelocityY, velocity.z);
        isGroundSpoofed = true;
    }

    private double applyGravity(double currentVelocityY) {
        return currentVelocityY + gravityEffect;
    }

    private void simulateGroundState(ClientPlayerEntity player) {
        World world = player.getWorld();
        BlockPos blockBelow = new BlockPos((int) player.getX(), (int) (player.getY() - 1.0), (int) player.getZ());

        if (world.getBlockState(blockBelow).isOpaque()) {
            player.setOnGround(true);
            isGroundSpoofed = false;
        } else if (System.nanoTime() - lastGroundUpdate > groundSpoofInterval) {
            player.setOnGround(true);
            lastGroundUpdate = System.nanoTime();
        }
    }
}