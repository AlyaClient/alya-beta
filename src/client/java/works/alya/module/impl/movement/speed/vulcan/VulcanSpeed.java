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

package works.alya.module.impl.movement.speed.vulcan;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.mixin.client.accessors.LivingEntityJumpAccessor;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;
import works.alya.utilities.player.TimerUtility;

public class VulcanSpeed extends SubModule {
    public VulcanSpeed(Module parent) {
        super("Vulcan", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!event.isPre()) return;
        if(mc.player == null) return;

        if(mc.options.jumpKey.isPressed()) {
            MoveUtility.setSpeed(0.1f, false);
        }

        if(MoveUtility.isMoving()) {
            LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;
            livingEntityJumpAccessor.setJumpingCooldown(0);

            if(mc.player.isOnGround()) {
                mc.player.jump();
            }
        }
    };

    @Override
    public void onDisable() {
        TimerUtility.resetTimer();
        super.onDisable();
    }
}
