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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import works.alya.command.CommandRepository;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private static final CommandDispatcher<ServerCommandSource> COMMAND_DISPATCHER = new CommandDispatcher<>();

    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    static {
        CommandRepository.registerCommands(COMMAND_DISPATCHER);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if(this.chatInputSuggestor != null) {
            this.chatInputSuggestor.refresh();
        }
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        if(chatText.startsWith(".")) {
            MinecraftClient client = MinecraftClient.getInstance();

            try {
                if(addToHistory) {
                    client.inGameHud.getChatHud().addToMessageHistory(chatText);
                }

                if(client.player == null) return;
                String command = chatText.substring(1);
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

                COMMAND_DISPATCHER.execute(command, source);
            } catch(CommandSyntaxException e) {
                if(client.player != null) {
                    client.player.sendMessage(Text.literal(e.getMessage()), false);
                }
            }

            ci.cancel();
        }
    }
}
