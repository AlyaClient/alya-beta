/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.utilities.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@SuppressWarnings("unused")
public class MoveUtility {
    private static final double VANILLA_PLAYER_FALL_MOTION = -0.0784000015258789;
    private static final double VANILLA_PLAYER_SPEED = 0.11681545167924458;
    private static final double VANILLA_PLAYER_SPRINT_SPEED = 0.1523673105277881;

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

    public static float getForward(MinecraftClient mc) {
        if(mc.player == null) return 0.0f;

        return mc.player.input.getMovementInput().y;
    }

    public static float getStrafe(MinecraftClient mc) {
        if(mc.player == null) return 0.0f;

        return mc.player.input.getMovementInput().x;
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

        if(player == null) return;

        if((!airStrafeOverride && !player.isOnGround()) || player.isTouchingWater() || player.isInLava()) {
            return;
        }

        float forward = getForward(mc);
        float strafe = getStrafe(mc);
        float yaw = player.getYaw();

        if(forward == 0.0f && strafe == 0.0f) {
            player.setVelocity(0, player.getVelocity().y, 0);
            return;
        }

        if(forward != 0.0f) {
            if(strafe >= 1.0f) {
                yaw += (forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            } else if(strafe <= -1.0f) {
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

    /**
     * Adjusts the player's movement, allowing strafing behavior under specific conditions.
     *
     * @param strafe Determines whether strafing mode is enabled. If true, the method attempts
     *               to apply a strafing motion to the player's movement when conditions such
     *               as being in liquid and not on the ground are met.
     */
    public static void setStrafe(boolean strafe) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if(player == null) return;

        boolean isInLiquid = !player.isTouchingWater() && !player.isInLava();
        boolean playerMoveOk = !player.isOnGround() && !player.getAbilities().flying;
        boolean canStrafe = strafe && isInLiquid && playerMoveOk;

        if(canStrafe) {
            float forward = getForward(mc);
            float strafe_value = getStrafe(mc);
            float yaw = player.getYaw();

            if(forward != 0.0f) {
                if(strafe_value >= 1.0f) {
                    yaw += (forward > 0.0f ? -45 : 45);
                    strafe_value = 0.0f;
                } else if(strafe_value <= -1.0f) {
                    yaw += (forward > 0.0f ? 45 : -45);
                    strafe_value = 0.0f;
                }
                forward = (forward > 0.0f) ? 1.0f : -1.0f;
            }

            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));

            double speed = player.isSprinting() ? 0.2873 : 0.2;
            double xVelocity = forward * speed * cos + strafe_value * speed * sin;
            double zVelocity = forward * speed * sin - strafe_value * speed * cos;

            player.setVelocity(xVelocity, player.getVelocity().y, zVelocity);
        }
    }

    /**
     * Sets the player's motion in the X direction.
     *
     * @param x The motion value to set for the X axis.
     */
    public static void setMotionX(double x) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) return;

        ClientPlayerEntity player = mc.player;

        player.setVelocity(x, player.getVelocity().y, player.getVelocity().z);
    }

    /**
     * Sets the player's motion in the Y direction.
     *
     * @param y The motion value to set for the Y axis.
     */
    public static void setMotionY(double y) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) return;

        ClientPlayerEntity player = mc.player;

        player.setVelocity(player.getVelocity().x, y, player.getVelocity().z);
    }

    /**
     * Sets the player's motion in the Z direction.
     *
     * @param z The motion value to set for the Z axis.
     */
    public static void setMotionZ(double z) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) return;

        ClientPlayerEntity player = mc.player;

        player.setVelocity(player.getVelocity().x, player.getVelocity().y,z);
    }

    /**
     * Retrieves the default falling speed value used for players.
     *
     * @return The standard falling motion value defined for vanilla player mechanics.
     */
    public static double getVanillaFallingSpeed() {
        return VANILLA_PLAYER_FALL_MOTION;
    }

    /**
     * Retrieves the default movement speed value for a vanilla player.
     *
     * @return The standard speed value for vanilla player movement.
     */
    public static double getVanillaPlayerSpeed() {
        return VANILLA_PLAYER_SPEED;
    }

    /**
     * Retrieves the default sprinting speed value for a vanilla player.
     *
     * @return The standard sprint speed defined for vanilla player mechanics.
     */
    public static double getVanillaPlayerSprintSpeed() {
        return VANILLA_PLAYER_SPRINT_SPEED;
    }

    /**
     * Calculates the square of the horizontal speed of the player.
     * The computation is based on the player's velocity along the X and Z axes.
     * If the player instance is null, the method will return 0.0.
     *
     * @return The squared horizontal speed of the player, or 0.0 if the player is not present.
     */
    public static double getSpeed() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;

        if(player == null) return 0.0;

        double motionX = player.getVelocity().x;
        double motionZ = player.getVelocity().z;

        return motionX * motionX + motionZ * motionZ;
    }
}
