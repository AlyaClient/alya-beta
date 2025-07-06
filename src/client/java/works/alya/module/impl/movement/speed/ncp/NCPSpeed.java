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

package works.alya.module.impl.movement.speed.ncp;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.mixin.client.accessors.LivingEntityJumpAccessor;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.misc.ChatUtility;
import works.alya.utilities.player.MoveUtility;

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