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

package dev.thoq.module.impl.movement.speed.normal;

import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class NormalSpeed {
    public void normalSpeed(MinecraftClient mc, GameOptions options, float speed, boolean bHop, boolean strafe) {
        if(mc.player == null) return;

        if(options.jumpKey.isPressed())
            speed = speed / 2;

        if(bHop && mc.player.isOnGround() && MoveUtility.isMoving())
            mc.player.jump();

        if(strafe)
            MoveUtility.setSpeed(speed, true);
        else
            MoveUtility.setSpeed(speed);
    }
}
