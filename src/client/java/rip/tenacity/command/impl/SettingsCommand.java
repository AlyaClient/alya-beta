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

package rip.tenacity.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import rip.tenacity.command.AbstractCommand;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleRepository;
import rip.tenacity.config.setting.Setting;
import rip.tenacity.utilities.misc.ChatUtility;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import java.util.concurrent.CompletableFuture;

public class SettingsCommand extends AbstractCommand {
    private final ModuleRepository moduleRepository;

    public SettingsCommand() {
        super("settings");
        this.moduleRepository = ModuleRepository.getInstance();
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(CommandManager.argument("module", StringArgumentType.word())
                        .suggests(this::suggestModules)
                        .then(CommandManager.argument("setting", StringArgumentType.word())
                                .suggests(this::suggestSettings)
                                .then(CommandManager.argument("value", StringArgumentType.greedyString())
                                        .executes(this::setSetting)))
                        .executes(this::listSettings))
                .executes(this::usage);
    }

    private int setSetting(CommandContext<ServerCommandSource> context) {
        String moduleName = StringArgumentType.getString(context, "module");
        String settingName = StringArgumentType.getString(context, "setting");
        String valueStr = StringArgumentType.getString(context, "value");

        Module module = moduleRepository.getModuleByName(moduleName);
        if(module == null) {
            ChatUtility.sendError("Module '" + moduleName + "' not found!");
            return 0;
        }

        Setting<?> setting = module.getSetting(settingName);
        if(setting == null) {
            ChatUtility.sendError("Setting '" + settingName + "' not found in module '" + moduleName + "'!");
            return 0;
        }

        try {
            setting.setValueFromObject(switch(setting.getValue()) {
                case Float ignored -> Float.parseFloat(valueStr);
                case Integer ignored -> Integer.parseInt(valueStr);
                case Boolean ignored -> Boolean.parseBoolean(valueStr);
                case Double ignored -> Double.parseDouble(valueStr);
                case String ignored -> valueStr;
                default -> throw new IllegalArgumentException("Unsupported setting type");
            });

            ChatUtility.sendPrefixedMessage(
                    "Settings",
                    String.format("Set %s.%s to %s", moduleName, settingName, valueStr),
                    Formatting.GOLD,
                    Formatting.GREEN
            );
            return 1;
        } catch(Exception ex) {
            ChatUtility.sendError("Invalid value format: " + ex.getMessage());
            return 0;
        }
    }

    private int listSettings(CommandContext<ServerCommandSource> context) {
        String moduleName = StringArgumentType.getString(context, "module");
        Module module = moduleRepository.getModuleByName(moduleName);

        if(module == null) {
            ChatUtility.sendError("Module '" + moduleName + "' not found!");
            return 0;
        }

        ChatUtility.sendPrefixedMessage(
                "Settings",
                "Settings for " + moduleName + ":",
                Formatting.GOLD,
                Formatting.WHITE
        );

        for(Setting<?> setting : module.getSettings()) {
            ChatUtility.sendMessage(String.format(" - %s: %s (default: %s)",
                    setting.getName(),
                    setting.getValue(),
                    setting.getDefaultValue()));
        }

        return 1;
    }

    private int usage(CommandContext<ServerCommandSource> context) {
        ChatUtility.sendPrefixedMessage(
                "Settings",
                "Opening settings UI. Usage: /settings <module> [setting] [value]",
                Formatting.GOLD,
                Formatting.WHITE
        );
        return 1;
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

    private CompletableFuture<Suggestions> suggestSettings(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String moduleName = StringArgumentType.getString(context, "module");
        Module module = moduleRepository.getModuleByName(moduleName);

        if(module != null) {
            String input = builder.getRemaining().toLowerCase();
            for(Setting<?> setting : module.getSettings()) {
                if(setting.getName().toLowerCase().startsWith(input)) {
                    builder.suggest(setting.getName());
                }
            }
        }
        return builder.buildFuture();
    }
}
