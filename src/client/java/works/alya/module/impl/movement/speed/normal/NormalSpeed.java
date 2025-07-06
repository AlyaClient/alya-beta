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

package works.alya.module.impl.movement.speed.normal;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

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