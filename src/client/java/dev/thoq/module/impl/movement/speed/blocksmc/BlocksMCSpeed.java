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

package dev.thoq.module.impl.movement.speed.blocksmc;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import dev.thoq.utilities.player.MoveUtility;

public class BlocksMCSpeed extends SubModule {

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