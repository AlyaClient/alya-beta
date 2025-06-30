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

package dev.thoq.module.impl.movement.highjump;

import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.predicate.entity.MovementPredicate;

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
