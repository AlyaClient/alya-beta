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

package works.alya.module.impl.movement.speed.spartan;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

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
