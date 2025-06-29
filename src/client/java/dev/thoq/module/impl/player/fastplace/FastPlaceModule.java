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

package dev.thoq.module.impl.player.fastplace;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.mixin.client.MinecraftClientMixin;
import dev.thoq.mixin.client.accessors.MinecraftClientAccessor;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.misc.ChatUtility;


public class FastPlaceModule extends Module {

    public FastPlaceModule() {
        super("FastPlace", "Helicopter helicopter", ModuleCategory.PLAYER);
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;
        ((MinecraftClientAccessor) this.mc).setItemUseCooldown(0);
    };

}