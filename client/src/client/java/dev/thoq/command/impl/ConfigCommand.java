package dev.thoq.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.thoq.config.ConfigManager;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import dev.thoq.command.AbstractCommand;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

@SuppressWarnings("SameParameterValue")
public class ConfigCommand extends AbstractCommand {

    public ConfigCommand() {
        super("config");
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(literal("save")
                        .then(argument("name", string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    ConfigManager.saveConfig(name);
                                    ChatUtility.sendSuccess("§aConfig §e" + name + " §asaved successfully!");
                                    return 1;
                                })))
                .then(literal("load")
                        .then(argument("name", string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    ConfigManager.loadConfig(name);
                                    ChatUtility.sendSuccess("§aConfig §e" + name + " §aloaded successfully!");
                                    return 1;
                                })))
                .then(literal("list")
                        .executes(context -> {
                            String[] configs = ConfigManager.listConfigs();
                            if(configs.length == 0) {
                                ChatUtility.sendError("§cNo configs found!");
                                return 0;
                            }
                            context.getSource().sendMessage(Text.of("§aAvailable configs:"));
                            for(String config : configs) {
                                ChatUtility.sendMessage("§7- §e" + config);
                            }
                            return 1;
                        }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> argument(String name, StringArgumentType type) {
        return com.mojang.brigadier.builder.RequiredArgumentBuilder.argument(name, type);
    }
}
