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

package dev.thoq.module.impl.movement.speed.normal;

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class NormalSpeed {
    public static void normalSpeed(MinecraftClient mc, GameOptions options, float speed, boolean bHop, boolean strafe) {
        if(mc.player == null) return;

        if(options.jumpKey.isPressed())
            speed = speed / 2;

        if(bHop && mc.player.isOnGround() && MovementUtility.isMoving())
            mc.player.jump();

        if(strafe)
            MovementUtility.setSpeed(speed, true);
        else
            MovementUtility.setSpeed(speed);
    }
}
