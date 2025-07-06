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

package works.alya.module.impl.utility.nofall;

import works.alya.config.setting.impl.ModeSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.utility.nofall.vanilla.VanillaNoFall;
import works.alya.module.impl.utility.nofall.verus.VerusNoFall;

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