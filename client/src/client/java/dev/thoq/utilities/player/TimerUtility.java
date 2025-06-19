package dev.thoq.utilities.player;

public class TimerUtility {
    private static float timerSpeed = 1.0f;

    public static void setTimerSpeed(float speed) {
        if(speed < 0.1f) speed = 0.1f;
        timerSpeed = speed;
    }

    public static void setTimerSpeed(double speed) {
        if(speed < 0.1f) speed = 0.1f;
        timerSpeed = (float) speed;
    }

    public static float getTimerSpeed() {
        return timerSpeed;
    }

    public static void resetTimer() {
        timerSpeed = 1.0f;
    }
}