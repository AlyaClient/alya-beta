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

package rip.tenacity.module.impl.movement.speed.ncp;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.mixin.client.accessors.LivingEntityJumpAccessor;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.misc.ChatUtility;
import rip.tenacity.utilities.player.MoveUtility;

public class NCPSpeed extends SubModule {
    static int ticksRunning = 0;

    public NCPSpeed(final Module parent) {
        super("NCP", parent);
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;

        boolean damaged = mc.player.hurtTime > 0;
        boolean isOnGround = mc.player.isOnGround();
        LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;

        livingEntityJumpAccessor.setJumpingCooldown(0);

        if(this.mc.options.jumpKey.isPressed())
            return;

        if(!MoveUtility.isMoving())
            reset();

        if(MoveUtility.isMoving()) {
            ticksRunning++;

            ChatUtility.sendDebug(">ticksRunning<: " + ticksRunning);

            if(isOnGround) {
                MoveUtility.setSpeed(0.28335f, false);
            } else {
                MoveUtility.setSpeed(0.28f, false);
            }

            if(isOnGround) {
                mc.player.jump();
                reset();
            } else if(MoveUtility.isMoving() && ticksRunning == 6 && !damaged) {
                MoveUtility.setMotionY(-0.186567865);
                ChatUtility.sendDebug("PULLED");
            }
        }
    };


    public void reset() {
        ticksRunning = 0;
    }
}