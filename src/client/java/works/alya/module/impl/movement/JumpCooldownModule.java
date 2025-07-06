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

package works.alya.module.impl.movement;

import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.mixin.client.accessors.LivingEntityJumpAccessor;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;

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
