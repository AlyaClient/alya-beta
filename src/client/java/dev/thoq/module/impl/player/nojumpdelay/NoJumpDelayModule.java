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

package dev.thoq.module.impl.player.nojumpdelay;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.mixin.client.accessors.LivingEntityJumpAccessor;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class NoJumpDelayModule extends Module {
    public NoJumpDelayModule() {
        super("NoJumpDelay", "Makes player a bouncy ball", ModuleCategory.PLAYER);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        if(mc.player.isOnGround()) {
            LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;
            livingEntityJumpAccessor.setJumpingCooldown(0);
        }
    };
}
