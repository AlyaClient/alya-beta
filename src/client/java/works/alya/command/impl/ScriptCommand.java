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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import works.alya.AlyaClient;
import works.alya.command.AbstractCommand;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.script.core.ScriptManager;
import works.alya.utilities.misc.ChatUtility;

/**
 * Command for managing scripts in the Alya Client.
 */
public class ScriptCommand extends AbstractCommand {

    public ScriptCommand() {
        super("scripts");
    }

    @Override
    protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder
                .then(literal("reload")
                        .executes(context -> {
                            for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
                                if(module.getCategory().equals(ModuleCategory.SCRIPTS)) {
                                    module.setEnabled(false);
                                }
                            }

                            ScriptManager.getInstance().loadScripts();
                            ChatUtility.sendSuccess("§aScripts reloaded successfully!");
                            return 1;
                        }));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        if(name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty!");

        return LiteralArgumentBuilder.literal(name);
    }
}