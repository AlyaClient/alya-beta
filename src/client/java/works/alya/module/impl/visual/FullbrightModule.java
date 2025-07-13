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

package works.alya.module.impl.visual;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;

public class FullbrightModule extends Module {
    private double previousGamma;

    public FullbrightModule() {
        super("FullBright", "Full Bright", "Light mode for minecraft caves", ModuleCategory.VISUAL);

        ModeSetting modeSetting = new ModeSetting("Mode", "Mode", "Gamma", "Gamma", "Potion");

        addSetting(modeSetting);
    }

    // 1726, 104
    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        switch(mode) {
            case "Gamma": {
                mc.options.getGamma().setValue(1.0D);

                break;
            }

            case "Potion": {
                StatusEffectInstance effect = new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        1000000000,
                        255,
                        false,
                        false
                );
                mc.player.addStatusEffect(effect);

                break;
            }
        }
    };

    @Override
    public void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        if(mc.player == null) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        switch(mode) {
            case "Gamma": {
                mc.options.getGamma().setValue(previousGamma);
                break;
            }
            case "Potion": {
                mc.player.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.NIGHT_VISION);
                break;
            }
        }

        super.onDisable();
    }
}
