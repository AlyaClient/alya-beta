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

package works.alya.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;

public abstract class AbstractCommand {
    private final String name;
    private final int permissionLevel;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected AbstractCommand(final String name, final int permissionLevel) {
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    protected AbstractCommand(final String name) {
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
    protected abstract void build(final LiteralArgumentBuilder<ServerCommandSource> builder);

    /**
     * Registers the command
     * @param dispatcher Command dispatcher
     */
    public void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder
                .<ServerCommandSource>literal(getName())
                .requires(source -> source.hasPermissionLevel(getPermissionLevel()));

        build(builder);
        dispatcher.register(builder);
    }
}
