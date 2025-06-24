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

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class NormalFlight {
    public static void normalFlight(
            MinecraftClient mc,
            GameOptions options,
            float speed,
            boolean verticalEnabled,
            boolean preventVanillaKick
    ) {
        if(mc.player == null) return;

        boolean up = options.jumpKey.isPressed();
        boolean down = options.sneakKey.isPressed();
        boolean verticalMovement = up || down;

        if(verticalEnabled) {
            if(up)
                MovementUtility.setMotionY(speed / 2);
            else if(down)
                MovementUtility.setMotionY(-speed / 2);
        }

        if(preventVanillaKick && !verticalMovement)
            MovementUtility.setMotionY(MovementUtility.getVanillaFallingSpeed());
        else if(!verticalMovement)
            MovementUtility.setMotionY(0);

        MovementUtility.setSpeed(speed * 2);
    }
}
