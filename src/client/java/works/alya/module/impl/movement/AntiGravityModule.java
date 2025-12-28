/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.movement;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.player.TimerUtility;

public final class AntiGravityModule extends Module {
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
