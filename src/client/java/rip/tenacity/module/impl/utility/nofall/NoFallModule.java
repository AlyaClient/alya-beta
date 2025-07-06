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

package rip.tenacity.module.impl.utility.nofall;

import rip.tenacity.config.setting.impl.ModeSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.utility.nofall.vanilla.VanillaNoFall;
import rip.tenacity.module.impl.utility.nofall.verus.VerusNoFall;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall", "No Fall", "Prevents fall damage", ModuleCategory.UTILITY);

        ModeSetting mode = new ModeSetting("Mode", "NoFall mode", "Vanilla", "Vanilla", "Verus");

        addSetting(mode);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        setPrefix(mode);

        switch(mode) {
            case "Vanilla": {
                VanillaNoFall.vanillaNoFall(mc);
                break;
            }

            case "Verus": {
                VerusNoFall.verusNoFall(mc);
                break;
            }
        }
    };
}