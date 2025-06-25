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
    private static int accumulatedTicks = 0;
    private static boolean isAccumulating = false;
    private static boolean isReleasing = false;
    private static int autoTriggerTimer = 0;
    private static int releaseTicks = 0;

    public static void ncpSpeed(MinecraftClient mc, GameOptions options, boolean damageBoost) {
        if(mc.player == null) return;
        boolean debug = RyeClient.INSTANCE.getModuleRepository().getModule(DebugModule.class).isEnabled();

        if(options.jumpKey.isPressed())
            return;

        MovementUtility.setSpeed(0.28f, false);

        if(MovementUtility.isMoving() && mc.player.isOnGround())
            mc.player.jump();

        if(damageBoost && mc.player.hurtTime > 0) {
            MovementUtility.setSpeed((double) mc.player.hurtTime / 2, false);
        }

        if(MovementUtility.isMoving()) {
            autoTriggerTimer++;
            if(autoTriggerTimer >= 10 && !isAccumulating && !isReleasing) {
                startAccumulation();
                autoTriggerTimer = 0;
            }

            if(isAccumulating) {
                if(debug) ChatUtility.sendDebug("accumulating...");
                handleAccumulation();
            } else if(isReleasing) {
                handleRelease();
                if(debug) ChatUtility.sendDebug("teleported");
            }

            if(TimerUtility.getTimerSpeed() > 2) {
                TimerUtility.setTimerSpeed(0.5);
            }
        } else {
            TimerUtility.resetTimer();
        }
    }

    private static void startAccumulation() {
        isAccumulating = true;
        accumulatedTicks = 0;
        TimerUtility.setTimerSpeed(0.6f);
    }

    private static void handleAccumulation() {
        accumulatedTicks++;

        if(accumulatedTicks >= 5) {
            isAccumulating = false;
            isReleasing = true;
            releaseTicks = 0;
            TimerUtility.setTimerSpeed(1.6f);
        }
    }

    private static void handleRelease() {
        releaseTicks++;

        if(releaseTicks >= accumulatedTicks) {
            isReleasing = false;
            TimerUtility.resetTimer();
            accumulatedTicks = 0;
            releaseTicks = 0;
        }
    }
}
