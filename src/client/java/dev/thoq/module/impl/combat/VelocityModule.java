/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.combat;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.module.impl.visual.DebugModule;
import dev.thoq.utilities.misc.ChatUtility;

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

    @Override
    protected void onPreTick() {
        if(!isEnabled() || mc.player == null) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        boolean debug = ModuleRepository.getInstance().getModule(DebugModule.class).isEnabled();

        switch(mode) {
            case "Standard": {
                float xz = ((NumberSetting<Float>) getSetting("Horizontal")).getValue();
                float y = ((NumberSetting<Float>) getSetting("Vertical")).getValue();

                if(mc.player.hurtTime > 0) {
                    if(debug) ChatUtility.sendDebug("player velocity >modified<");
                    mc.player.setVelocity(
                            mc.player.getVelocity().x * xz,
                            mc.player.getVelocity().y * y,
                            mc.player.getVelocity().z * xz
                    );
                }
                break;
            }

            case "Jump Reset": {
                if(mc.player.hurtTime > 0 && mc.player.isOnGround()) {
                    mc.player.jump();
                }

                break;
            }

            case "Hurt Time": {
                double multiplierXZ = 0.6 / mc.player.hurtTime;
                double multiplierY = 0.9 / mc.player.hurtTime;

                if(mc.player.hurtTime > 0) {
                    if(debug) ChatUtility.sendDebug("player velocity >modified<");
                    if(debug) ChatUtility.sendDebug(String.format("[multiplierXZ: %f], [multiplierY: %f]", multiplierXZ, multiplierY));
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
}
