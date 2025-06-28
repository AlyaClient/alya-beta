/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;

public abstract class AbstractCommand {
    private final String name;
    private final int permissionLevel;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected AbstractCommand(String name, int permissionLevel) {
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    protected AbstractCommand(String name) {
        this(name, 0);
    }

    /**
     * Gets the command name
     * @return command name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the required permission level
     * @return permission level
     */
    public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * Build the command logic
     * @param builder The command builder
     */
    protected abstract void build(LiteralArgumentBuilder<ServerCommandSource> builder);

    /**
     * Registers the command
     * @param dispatcher Command dispatcher
     */
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder
                .<ServerCommandSource>literal(getName())
                .requires(source -> source.hasPermissionLevel(getPermissionLevel()));

        build(builder);
        dispatcher.register(builder);
    }
}
