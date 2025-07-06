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

package rip.tenacity.module.impl.movement.speed.spartan;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.player.MoveUtility;

public class SpartanSpeed extends SubModule {
    private int ticks = 0;

    public SpartanSpeed(Module parent) {
        super("Spartan", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;

        if(mc.player.isOnGround()) {
            ticks = 0;
        } else {
            ticks++;

            if(ticks == 1) {
                if(MoveUtility.getSpeed() < 0.8) {
                    MoveUtility.setSpeed(0.8, true);
                } else {
                    MoveUtility.setSpeed(MoveUtility.getSpeed() * 2.5, true);
                }
            }
        }
    };

    @Override
    public void reset() {
        ticks = 0;
    }
}
