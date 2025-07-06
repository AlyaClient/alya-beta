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
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class VClipCommand extends AbstractCommand {
    public VClipCommand() {
        super("vclip");
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(CommandManager.argument("distance", StringArgumentType.word())
                        .executes(context -> {
                            String distanceStr = StringArgumentType.getString(context, "distance");
                            try {
                                int distance = Integer.parseInt(distanceStr);
                                teleport(distance);
                                return 1;
                            } catch(NumberFormatException e) {
                                return 0;
                            }
                        }))
                .executes(this::usage);
    }

    private int usage(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        ChatUtility.sendInfo("Usage: .vclip <distance>");
        return 1;
    }

    private static void teleport(int distance) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if(mc.player == null) return;

        double x = mc.player.getPos().x;
        double y = mc.player.getPos().y + distance;
        double z = mc.player.getPos().z;

        mc.player.setPosition(x, y, z);
    }
}
