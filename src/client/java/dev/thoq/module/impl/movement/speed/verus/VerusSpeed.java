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

import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.module.speed.TickbaseUtility;
import dev.thoq.utilities.player.MoveUtility;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class VerusSpeed {
    private final TickbaseUtility tickbaseUtility = new TickbaseUtility();
    private static int autoTriggerTimer = 0;

    public void verusSpeed(MinecraftClient mc, GameOptions options, boolean verusDamageBoost) {
        if(mc.player == null) return;
        boolean forwardOnly = options.forwardKey.isPressed() && !options.backKey.isPressed() && !options.leftKey.isPressed() && !options.rightKey.isPressed();

        if(options.jumpKey.isPressed())
            return;

        if(forwardOnly)
            MoveUtility.setSpeed(0.34f, true);
        else
            MoveUtility.setSpeed(0.26f, false);

        if(MoveUtility.isMoving() && mc.player.isOnGround())
            mc.player.jump();

        if(verusDamageBoost && mc.player.hurtTime > 0) {
            MoveUtility.setSpeed((double) mc.player.hurtTime / 2, false);
        }

        if(MoveUtility.isMoving()) {
            autoTriggerTimer++;
            if(autoTriggerTimer >= 5 && !TickbaseUtility.isAccumulating && !TickbaseUtility.isReleasing) {
                tickbaseUtility.startAccumulation();
                autoTriggerTimer = 0;
            }

            if(TickbaseUtility.isAccumulating) {
                ChatUtility.sendDebug("accumulating...");
                tickbaseUtility.handleAccumulation(2);
            } else if(TickbaseUtility.isReleasing) {
                tickbaseUtility.handleRelease();
                ChatUtility.sendDebug("teleported");
            }

            if(TimerUtility.getTimerSpeed() > 3) {
                TimerUtility.setTimerSpeed(0.9);
            }
        } else {
            TimerUtility.resetTimer();
        }
    }
}
