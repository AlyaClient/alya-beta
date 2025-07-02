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

package dev.thoq.module.impl.combat;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class AttackDelayModule extends Module {
    private final ModeSetting modeSetting = new ModeSetting("Delay", "The delay to use", "1.16.5", "1.7", "1.8", "1.16.5");

    public AttackDelayModule() {
        super("AttackDelay", "Attack Delay", "Change the attack delay", ModuleCategory.COMBAT);
        addSetting(modeSetting);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null) return;

        String mode = modeSetting.getValue();

        switch(mode) {
            case "1.7": {
                mc.player.resetLastAttackedTicks();
                break;
            }
            case "1.8": {
                if(mc.player.getAttackCooldownProgress(0.0f) < 0.9f) {
                    mc.player.resetLastAttackedTicks();
                }
                break;
            }
            case "1.16.5": {
                // Don't reset attack ticks - respect vanilla weapon cooldown
                break;
            }
        }
    };

    public boolean canAttack() {
        if(mc.player == null) return false;

        String mode = modeSetting.getValue();

        return switch(mode) {
            case "1.7" -> true; // no delay
            case "1.8" -> mc.player.getAttackCooldownProgress(0.0f) >= 0.1f; // Minimal delay
            case "1.16.5" -> mc.player.getAttackCooldownProgress(0.0f) >= 1.0f; // Full weapon cooldown
            default -> mc.player.getAttackCooldownProgress(0.0f) >= 1.0f;
        };
    }

    public boolean isNewPvpDelay() {
        return modeSetting.getValue().equals("1.16.5");
    }
}