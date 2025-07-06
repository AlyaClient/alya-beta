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
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.player.MoveUtility;

public class HighJumpModule extends Module {
    private final NumberSetting<Float> height = new NumberSetting<>("Height", "Height of the jump", 1.0f, 0.1f, 10.0f);

    public HighJumpModule() {
        super("HighJump", "High Jump", "Makes you jump very high", ModuleCategory.MOVEMENT);

        addSetting(height);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || !event.isPre()) return;

        float jumpHeight = height.getValue();

        if(mc.player.isOnGround() && mc.player.isJumping()) {
            MoveUtility.setMotionY(jumpHeight);
        }
    };
}
