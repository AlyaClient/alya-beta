/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.module.impl.player.nofall;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.player.nofall.vanilla.VanillaNoFall;
import dev.thoq.module.impl.player.nofall.verus.VerusNoFall;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall", "Prevents fall damage", ModuleCategory.PLAYER);

        ModeSetting mode = new ModeSetting("Mode", "NoFall mode", "Vanilla", "Vanilla", "Verus");

        addSetting(mode);
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
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