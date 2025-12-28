/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.visual;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;

public final class DiscordRPCModule extends Module {
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
