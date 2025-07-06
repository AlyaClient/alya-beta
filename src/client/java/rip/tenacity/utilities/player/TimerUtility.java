/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.utilities.player;

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