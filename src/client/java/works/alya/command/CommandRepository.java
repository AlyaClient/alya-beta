/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public final class CommandRepository {
    private static final List<AbstractCommand> commands = new ArrayList<>();

    public static void registerCommand(final AbstractCommand command) {
        commands.add(command);
    }

    public static void registerCommands(final CommandDispatcher<ServerCommandSource> dispatcher) {
        for(final AbstractCommand command : commands) {
            command.register(dispatcher);
        }
    }
}
