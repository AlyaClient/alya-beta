/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.movement.flight.vanilla;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import net.minecraft.util.math.Vec3d;

public final class CreativeFlight extends SubModule {

    private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Flight speed multiplier", 1.5F, 0.1F, 10.0F);
    private final BooleanSetting verticalEnabled = new BooleanSetting("Vertical", "Enable Vertical movement", true);

    public CreativeFlight(final Module parent) {
        super("Creative", parent);
        this.addSettings(this.speed, this.verticalEnabled);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null) return;

        this.mc.player.getAbilities().flying = true;
        this.mc.player.getAbilities().setFlySpeed(this.speed.getValue() * 0.05F);

        if(!this.verticalEnabled.getValue()) {
            if(this.mc.options.jumpKey.isPressed() || this.mc.options.sneakKey.isPressed()) {
                Vec3d vel = this.mc.player.getVelocity();
                this.mc.player.setVelocity(vel.x, 0, vel.z);
            }
        }
    };

}
