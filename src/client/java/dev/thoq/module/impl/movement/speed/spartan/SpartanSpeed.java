/*
 * Copyright (c) Rye Client 2024-2025.
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

package dev.thoq.module.impl.movement.speed.spartan;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MoveUtility;

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
