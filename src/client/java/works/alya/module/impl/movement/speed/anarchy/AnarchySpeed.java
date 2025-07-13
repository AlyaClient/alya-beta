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

package works.alya.module.impl.movement.speed.anarchy;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

public class AnarchySpeed extends SubModule {
    private static int timeElapsed = 0;

    public AnarchySpeed(Module parent) {
        super("Anarchy", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;
        timeElapsed++;

        if(mc.player.isOnGround() && MoveUtility.isMoving()) {
            mc.player.jump();
        }

        if(timeElapsed >= 10) {
            MoveUtility.setSpeed(1f, true);
        } else {
            MoveUtility.setSpeed(0.28f, true);
        }

        if(timeElapsed > 11) {
            timeElapsed = 0;
        }
    };

    @Override
    public void reset() {
        super.reset();

        timeElapsed = 0;
    }
}