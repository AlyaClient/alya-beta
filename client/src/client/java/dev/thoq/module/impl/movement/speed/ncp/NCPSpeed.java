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

package dev.thoq.module.impl.movement.speed.ncp;

import dev.thoq.RyeClient;
import dev.thoq.module.impl.visual.DebugModule;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MovementUtility;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class NCPSpeed {
    private static float timeRunning = 0;

    public static void ncpSpeed(MinecraftClient mc, GameOptions options, boolean damageBoost) {
        if(mc.player == null) return;
        boolean debug = RyeClient.INSTANCE.getModuleRepository().getModule(DebugModule.class).isEnabled();

        if(options.jumpKey.isPressed())
            MovementUtility.setSpeed(0.2f, false);

        MovementUtility.setSpeed(0.26f, false);

        if(MovementUtility.isMoving() && mc.player.isOnGround())
            mc.player.jump();

        if(damageBoost && mc.player.hurtTime > 0) {
            MovementUtility.setSpeed((double) mc.player.hurtTime / 2, false);
        }

        if(MovementUtility.isMoving()) {
            if(debug) ChatUtility.sendDebug("ticking: " + timeRunning + "...");
            timeRunning++;

            if(timeRunning > 10) {
                TimerUtility.setTimerSpeed(1.1f);
            }

            if(timeRunning > 20) {
                TimerUtility.setTimerSpeed(timeRunning / 40.0f);
            }

            if(timeRunning > 50) timeRunning = 0;
        } else {
            timeRunning = 0;
            TimerUtility.resetTimer();
        }
    }
}
