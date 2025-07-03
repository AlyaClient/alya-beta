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

package dev.thoq.module.impl.movement.speed.normal;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import dev.thoq.utilities.player.MoveUtility;

public class NormalSpeed extends SubModule {

    private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Zoom speed multiplier", 1.5F, 0.5F, 10.0F);
    private final BooleanSetting bHop = new BooleanSetting("BHop", "Enable BHop?", true);
    private final BooleanSetting strafe = new BooleanSetting("Strafe", "Enable Strafe?", true);

    public NormalSpeed(final Module parent) {
        super("Normal", parent);
        this.addSettings(this.speed, this.bHop, this.strafe);
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;

        if(this.bHop.getValue() && mc.player.isOnGround() && MoveUtility.isMoving()) {
            if (!this.mc.options.jumpKey.isPressed()) this.mc.player.jump();
        }

        if(this.strafe.getValue())
            MoveUtility.setSpeed(this.speed.getValue(), true);
        else
            MoveUtility.setSpeed(this.speed.getValue());
    };

}