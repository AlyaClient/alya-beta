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

package rip.tenacity.module.impl.visual;

import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;

public class DiscordRPCModule extends Module {
    // private static int ticksRunning = 0;

    public DiscordRPCModule() {
        super("DiscordRPC", "Discord RPC", "Shows client status in Discord", ModuleCategory.VISUAL);
    }

//    private final IEventListener<TickEvent> tickEvent = event -> {
//        ticksRunning++;
//
//        if(ticksRunning >= 40) {
//            DiscordIntegration.updatePresence();ryecl
//            ticksRunning = 0;
//        }
//    };
}
