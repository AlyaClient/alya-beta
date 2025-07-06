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

package works.alya.module.impl.utility.fastplace;

import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.mixin.client.accessors.MinecraftClientAccessor;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;

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