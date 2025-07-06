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

package works.alya.module.impl.movement.speed.verus;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

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