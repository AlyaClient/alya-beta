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
        TimerUtility.setTimerSpeed(0.6f);
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
