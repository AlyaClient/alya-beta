package dev.thoq.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.thoq.command.CommandRepository;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
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
