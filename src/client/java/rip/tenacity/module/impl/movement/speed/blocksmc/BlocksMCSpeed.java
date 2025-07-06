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

package rip.tenacity.module.impl.movement.speed.blocksmc;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.player.MoveUtility;

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