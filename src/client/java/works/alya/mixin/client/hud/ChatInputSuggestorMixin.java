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

package works.alya.mixin.client.hud;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import works.alya.command.CommandRepository;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {
    @Final
    @Shadow
    TextFieldWidget textField;
    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Unique
    private static final CommandDispatcher<ServerCommandSource> COMMAND_DISPATCHER = new CommandDispatcher<>();

    static {
        CommandRepository.registerCommands(COMMAND_DISPATCHER);
    }

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        String text = textField.getText();

        if(text.startsWith(".")) {
            MinecraftClient client = MinecraftClient.getInstance();
            if(client.player == null) return;

            ServerCommandSource source = new ServerCommandSource(
                    null,
                    client.player.getPos(),
                    client.player.getRotationClient(),
                    null,
                    0,
                    client.player.getName().getString(),
                    client.player.getName(),
                    null,
                    client.player
            );

            StringReader stringReader = new StringReader(text.substring(1));
            ParseResults<ServerCommandSource> parseResults;
            if(stringReader.canRead()) {
                parseResults = COMMAND_DISPATCHER.parse(stringReader, source);
            } else {
                parseResults = COMMAND_DISPATCHER.parse("", source);
            }

            pendingSuggestions = COMMAND_DISPATCHER.getCompletionSuggestions(parseResults);
            ci.cancel();
        }
    }
}
