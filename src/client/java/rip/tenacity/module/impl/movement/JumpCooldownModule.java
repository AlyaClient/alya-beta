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

package rip.tenacity.module.impl.movement;

import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.mixin.client.accessors.LivingEntityJumpAccessor;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;

// todo: borked
public class JumpCooldownModule extends Module {
    private final NumberSetting<Integer> cooldown = new NumberSetting<>("Cooldown", "Cooldown of the jump", 0, 0, 1);

    public JumpCooldownModule() {
        super("JumpCooldown", "Jump Cooldown", "Makes player a bouncy ball", ModuleCategory.MOVEMENT);

        addSetting(cooldown);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        int cooldownValue = cooldown.getValue();

        if(mc.player.isOnGround()) {
            LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;
            livingEntityJumpAccessor.setJumpingCooldown(cooldownValue);
        }
    };
}
