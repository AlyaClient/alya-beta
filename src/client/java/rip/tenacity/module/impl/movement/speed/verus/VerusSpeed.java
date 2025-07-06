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

package rip.tenacity.module.impl.movement.speed.verus;

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.player.MoveUtility;

public class VerusSpeed extends SubModule {

    private final BooleanSetting verusDamageBoost = new BooleanSetting("Damage boost", "Boost speed when damaged", true);

    public VerusSpeed(final Module parent) {
        super("Verus", parent);
        this.addSettings(this.verusDamageBoost);
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;
        boolean forwardOnly = this.mc.options.forwardKey.isPressed() && !this.mc.options.backKey.isPressed() && !this.mc.options.leftKey.isPressed() && !this.mc.options.rightKey.isPressed();

        if(this.mc.options.jumpKey.isPressed())
            return;

        if(forwardOnly)
            MoveUtility.setSpeed(0.285f, true);
        else
            MoveUtility.setSpeed(0.26f, true);

        if(MoveUtility.isMoving() && this.mc.player.isOnGround())
            this.mc.player.jump();

        if(this.verusDamageBoost.getValue() && this.mc.player.hurtTime > 0) {
            MoveUtility.setSpeed((double) this.mc.player.hurtTime / 2, true);
        }
    };

}