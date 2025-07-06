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

package rip.tenacity.module.impl.movement.speed.normal;

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.player.MoveUtility;

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