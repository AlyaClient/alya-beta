package dev.thoq.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.thoq.command.CommandRepository;
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
