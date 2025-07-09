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

package works.alya.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import works.alya.command.AbstractCommand;
import works.alya.utilities.misc.AlyaConstants;
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class NameCommand extends AbstractCommand {
    public NameCommand() {
        super("name");
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(context -> {
                            AlyaConstants.NAME = StringArgumentType.getString(context, "name");
                            return 1;
                        }))
                .executes(this::usage);
    }

    private int usage(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        ChatUtility.sendInfo("Usage: .name <name>");
        return 1;
    }
}
