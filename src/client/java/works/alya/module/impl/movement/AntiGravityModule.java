/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.module.impl.movement;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.player.TimerUtility;

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
