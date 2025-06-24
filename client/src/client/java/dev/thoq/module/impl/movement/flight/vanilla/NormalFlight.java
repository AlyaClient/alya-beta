/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.movement.flight.vanilla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;

public class NormalFlight {
    public static void normalFlight(MinecraftClient mc, GameOptions options, float speed, boolean verticalEnabled) {
        if(mc.player == null) return;

        mc.player.setVelocity(0, 0, 0);
        mc.player.setSprinting(false);

        double verticalSpeed = 0;
        if(verticalEnabled) {
            if(options.jumpKey.isPressed())
                verticalSpeed = speed / 2;
            else if(options.sneakKey.isPressed())
                verticalSpeed = -speed / 2;
        }

        float forward = options.forwardKey.isPressed() ? 1.0f : options.backKey.isPressed() ? -1.0f : 0.0f;
        float sideways = options.leftKey.isPressed() ? 1.0f : options.rightKey.isPressed() ? -1.0f : 0.0f;

        float yaw = mc.player.getYaw();
        double radianYaw = Math.toRadians(yaw);

        double x = 0;
        double z = 0;

        if(forward != 0 || sideways != 0) {
            x -= forward * Math.sin(radianYaw);
            z += forward * Math.cos(radianYaw);

            x += sideways * Math.cos(radianYaw);
            z += sideways * Math.sin(radianYaw);

            double length = Math.sqrt(x * x + z * z);

            if(length > 0) {
                x = x / length * speed;
                z = z / length * speed;
            }
        }

        mc.player.setVelocity(new Vec3d(x, verticalSpeed, z));
    }
}
