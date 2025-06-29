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

package dev.thoq.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.thoq.command.AbstractCommand;
import dev.thoq.config.KeybindManager;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;

public class BindCommand extends AbstractCommand {
    private final ModuleRepository moduleRepository;
    private final KeybindManager keybindManager;

    public BindCommand() {
        super("bind");
        this.moduleRepository = ModuleRepository.getInstance();
        this.keybindManager = KeybindManager.getInstance();
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(CommandManager.argument("module", StringArgumentType.word())
                        .suggests(this::suggestModules)
                        .then(CommandManager.argument("key", StringArgumentType.word())
                                .executes(this::bindKey)))
                .executes(this::listBinds);
    }

    private CompletableFuture<Suggestions> suggestModules(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        for(Module module : moduleRepository.getModules()) {
            if(module.getName().toLowerCase().startsWith(input)) {
                builder.suggest(module.getName());
            }
        }
        return builder.buildFuture();
    }

    private int bindKey(CommandContext<ServerCommandSource> context) {
        String moduleName = StringArgumentType.getString(context, "module");
        String keyName = StringArgumentType.getString(context, "key").toLowerCase();

        Module module = moduleRepository.getModuleByName(moduleName);
        if(module == null) {
            ChatUtility.sendError("Module '" + moduleName + "' not found!");
            return 0;
        }

        try {
            if(keyName.equalsIgnoreCase("none")) {
                keybindManager.unbind(module);  // Use unbind instead of bind with UNKNOWN
                ChatUtility.sendPrefixedMessage(
                        "Bind",
                        String.format("Unbound %s", module.getName()),
                        Formatting.GOLD,
                        Formatting.GREEN
                );
                return 1;
            }
            int keyCode = GLFW.class.getField("GLFW_KEY_" + keyName.toUpperCase()).getInt(null);
            keybindManager.bind(module, keyCode);
            ChatUtility.sendPrefixedMessage(
                    "Bind",
                    String.format("Bound %s to key %s", module.getName(), keyName.toUpperCase()),
                    Formatting.GOLD,
                    Formatting.GREEN
            );
            return 1;
        } catch(NoSuchFieldException | IllegalAccessException e) {
            ChatUtility.sendError("Invalid key name: " + keyName);
            return 0;
        }
    }

    private int listBinds(CommandContext<ServerCommandSource> context) {
        ChatUtility.sendPrefixedMessage("Binds", "Current keybindings:", Formatting.GOLD, Formatting.WHITE);
        for(Module module : moduleRepository.getModules()) {
            Integer key = keybindManager.getKeyForModule(module);
            if(key != null) {
                String keyName = net.minecraft.client.util.InputUtil.fromKeyCode(key, 0).getTranslationKey();
                ChatUtility.sendMessage(" - " + module.getName() + ": " + keyName.replace("key.keyboard.", "").toUpperCase());
            }
        }
        return 1;
    }
}