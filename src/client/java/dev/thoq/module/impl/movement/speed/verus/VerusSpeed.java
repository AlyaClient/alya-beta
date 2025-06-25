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

package dev.thoq.module.impl.movement.speed.verus;

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class VerusSpeed {
    public static void verusSpeed(MinecraftClient mc, GameOptions options, boolean verusDamageBoost) {
        if(mc.player == null) return;

        if(options.jumpKey.isPressed())
            MovementUtility.setSpeed(0.3f, true);

        if(mc.player.isOnGround() && MovementUtility.isMoving())
            mc.player.jump();

        if(verusDamageBoost && mc.player.hurtTime > 0)
            MovementUtility.setSpeed((double) mc.player.hurtTime / 2, true);

        MovementUtility.setSpeed(0.26f, true);
    }
}
