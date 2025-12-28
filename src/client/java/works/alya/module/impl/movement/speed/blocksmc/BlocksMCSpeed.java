/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.movement.speed.blocksmc;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

public final class BlocksMCSpeed extends SubModule {

    public BlocksMCSpeed(final Module parent) {
        super("BlocksMC", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;

        boolean damaged = mc.player.hurtTime > 0;
        boolean isOnGround = mc.player.isOnGround();

        if(this.mc.options.jumpKey.isPressed())
            return;

        if(MoveUtility.isMoving()) {
            if(isOnGround) {
                MoveUtility.setSpeed(0.28335f, false);
                mc.player.jump();
            } else {
                MoveUtility.setSpeed(0.28f, false);
            }

            if(damaged) {
                MoveUtility.setSpeed(0.3, true);
            }
        }
    };
}