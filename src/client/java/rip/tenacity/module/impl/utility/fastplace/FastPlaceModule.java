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

package rip.tenacity.module.impl.utility.fastplace;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.mixin.client.accessors.MinecraftClientAccessor;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;

public class FastPlaceModule extends Module {

    public FastPlaceModule() {
        super("FastPlace", "Fast Place", "Helicopter helicopter", ModuleCategory.UTILITY);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;
        ((MinecraftClientAccessor) this.mc).setItemUseCooldown(0);
    };

}