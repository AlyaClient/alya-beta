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

package rip.tenacity.module.impl.movement.flight.vanilla;

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import net.minecraft.util.math.Vec3d;

public class CreativeFlight extends SubModule {

    private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Flight speed multiplier", 1.5F, 0.1F, 10.0F);
    private final BooleanSetting verticalEnabled = new BooleanSetting("Vertical", "Enable Vertical movement", true);

    public CreativeFlight(final Module parent) {
        super("Creative", parent);
        this.addSettings(this.speed, this.verticalEnabled);
    }

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
