/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.utilities.module.speed;

import dev.thoq.utilities.player.TimerUtility;

public class TickbaseUtility {
    public static boolean isAccumulating = false;
    public static boolean isReleasing = false;
    private static int accumulatedTicks = 0;
    private static int releaseTicks = 0;

    public void startAccumulation() {
        isAccumulating = true;
        accumulatedTicks = 0;
        TimerUtility.setTimerSpeed(0.65f);
    }

    public void handleAccumulation(int releaseSpeed) {
        accumulatedTicks++;

        if(accumulatedTicks >= releaseSpeed) {
            isAccumulating = false;
            isReleasing = true;
            releaseTicks = 0;
            TimerUtility.setTimerSpeed(1.6f);
        }
    }

    public void handleRelease() {
        releaseTicks++;

        if(releaseTicks >= accumulatedTicks) {
            isReleasing = false;
            TimerUtility.resetTimer();
            accumulatedTicks = 0;
            releaseTicks = 0;
        }
    }
}
