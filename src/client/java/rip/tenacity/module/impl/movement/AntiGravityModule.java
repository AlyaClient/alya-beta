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

package rip.tenacity.module.impl.movement;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.player.TimerUtility;

public class AntiGravityModule extends Module {
    public AntiGravityModule() {
        super("AntiGravity", "Anti-Gravity", "Makes you feel like you're on the moon", ModuleCategory.MOVEMENT);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null) return;

        if(event.isPre()) {
            if(mc.player.isOnGround())
                TimerUtility.setTimerSpeed(0.97);
            else
                TimerUtility.setTimerSpeed(0.87658);
        }
    };

    @Override
    public void onDisable() {
        if(mc.player == null) return;

        TimerUtility.resetTimer();
    }
}
