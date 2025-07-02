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

package dev.thoq.module.impl.movement.speed.verus;

import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class VerusSpeed {
    public void verusSpeed(MinecraftClient mc, GameOptions options, boolean verusDamageBoost) {
        if(mc.player == null) return;
        boolean forwardOnly = options.forwardKey.isPressed() && !options.backKey.isPressed() && !options.leftKey.isPressed() && !options.rightKey.isPressed();

        if(options.jumpKey.isPressed())
            return;

        if(forwardOnly)
            MoveUtility.setSpeed(0.29f, true);
        else
            MoveUtility.setSpeed(0.26f, true);

        if(MoveUtility.isMoving() && mc.player.isOnGround())
            mc.player.jump();

        if(verusDamageBoost && mc.player.hurtTime > 0) {
            MoveUtility.setSpeed((double) mc.player.hurtTime / 2, true);
        }
    }
}