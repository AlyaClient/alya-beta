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

public class CreativeFlight {
    public static void creativeFlight(MinecraftClient mc, GameOptions options, float speed, boolean verticalEnabled) {
        if(mc.player == null) return;

        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed * 0.05f);

        if(!verticalEnabled) {
            if(options.jumpKey.isPressed() || options.sneakKey.isPressed()) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0, vel.z);
            }
        }
    }
}
