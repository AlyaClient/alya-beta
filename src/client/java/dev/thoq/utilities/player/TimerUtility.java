/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

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
        setTimerSpeed(1.0f);
    }
}