package dev.thoq.utilities.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MovementUtility {
    /**
     * Sets the player's movement speed
     *
     * @param speed The speed to set
     */
    public static void setSpeed(double speed) {
        setSpeed(speed, false);
    }

    /**
     * Determines if the player is currently moving by checking the velocity.
     *
     * @return true if the player has a significant velocity, false otherwise
     */
    public static boolean isMoving() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if(mc.player == null) return false;

        return mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0;
    }

    /**
     * Sets the player's movement speed
     *
     * @param speed The speed to set
     * @param airStrafeOverride Whether to override air strafe movement
     */
    public static void setSpeed(double speed, boolean airStrafeOverride) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if (player == null) return;

        if ((!airStrafeOverride && !player.isOnGround()) || player.isTouchingWater() || player.isInLava()) {
            return;
        }

        float forward = player.input.movementForward;
        float strafe = player.input.movementSideways;
        float yaw = player.getYaw();

        if (forward == 0.0f && strafe == 0.0f) {
            player.setVelocity(0, player.getVelocity().y, 0);
            return;
        }

        if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += (forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += (forward > 0.0f ? 45 : -45);
                strafe = 0.0f;
            }

            forward = (forward > 0.0f) ? 1.0f : -1.0f;
        }

        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));

        double xVelocity = forward * speed * cos + strafe * speed * sin;
        double zVelocity = forward * speed * sin - strafe * speed * cos;

        player.setVelocity(xVelocity, player.getVelocity().y, zVelocity);
    }
}
