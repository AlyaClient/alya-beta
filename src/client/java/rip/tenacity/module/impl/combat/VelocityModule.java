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

package rip.tenacity.module.impl.combat;

import rip.tenacity.config.setting.impl.ModeSetting;
import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.PacketReceiveEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.misc.ChatUtility;

@SuppressWarnings({"rawtypes", "unchecked", "FieldCanBeLocal"})
public class VelocityModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Different modes", "Standard", "Standard", "Jump Reset", "Hurt Time");
    private final NumberSetting horizontalVelocity = new NumberSetting("Horizontal", "X + Z velocity multiplier", 1.0f, 0f, 1.0f);
    private final NumberSetting verticalVelocity = new NumberSetting("Vertical", "Y velocity multiplier", 1.0f, 0f, 1.0f);

    public VelocityModule() {
        super("Velocity", "Makes you variably American", ModuleCategory.COMBAT);

        addSetting(mode);
        addSetting(horizontalVelocity.setVisibilityCondition(() -> "Standard".equals(mode.getValue())));
        addSetting(verticalVelocity.setVisibilityCondition(() -> "Standard".equals(mode.getValue())));
    }

    private final IEventListener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if(!isEnabled() || mc.player == null) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        if(mc.player.hurtTime > 0 && event.isPre()) {
            switch(mode) {
                case "Standard": {
                    float xz = ((NumberSetting<Float>) getSetting("Horizontal")).getValue();
                    float y = ((NumberSetting<Float>) getSetting("Vertical")).getValue();

                    ChatUtility.sendDebug("player velocity >modified<");
                    mc.player.setVelocity(
                            mc.player.getVelocity().x * xz,
                            mc.player.getVelocity().y * y,
                            mc.player.getVelocity().z * xz
                    );

                    event.cancel();

                    break;
                }

                case "Jump Reset": {
                    if(mc.player.isOnGround()) {
                        mc.player.jump();
                    }

                    break;
                }

                case "Hurt Time": {
                    double multiplierXZ = 0.6 / mc.player.hurtTime;
                    double multiplierY = 0.9 / mc.player.hurtTime;

                    if(mc.player.hurtTime > 0) {
                        ChatUtility.sendDebug("player velocity >modified<");
                        ChatUtility.sendDebug(String.format("[multiplierXZ: %f], [multiplierY: %f]", multiplierXZ, multiplierY));
                        mc.player.setVelocity(
                                mc.player.getVelocity().y * multiplierXZ,
                                mc.player.getVelocity().y * multiplierY,
                                mc.player.getVelocity().z * multiplierXZ
                        );
                    }

                    break;
                }
            }
        }
    };
}
