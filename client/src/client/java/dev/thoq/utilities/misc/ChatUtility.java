package dev.thoq.utilities.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtility {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Sends a formatted message to the player
     *
     * @param message    The message to send
     * @param formatting The color/formatting to apply
     */
    public static void sendMessage(String message, Formatting formatting) {
        if(mc.player != null) {
            MutableText text = Text.literal(message).setStyle(Style.EMPTY.withColor(formatting));
            mc.player.sendMessage(text, false);
        }
    }

    /**
     * Sends a regular message to chat
     *
     * @param message The message to send
     */
    public static void sendMessage(String message) {
        sendMessage(message, Formatting.WHITE);
    }

    /**
     * Sends an error message in red
     *
     * @param message The error message
     */
    public static void sendError(String message) {
        sendMessage("Error: " + message, Formatting.RED);
    }

    /**
     * Sends a success message in green
     *
     * @param message The success message
     */
    public static void sendSuccess(String message) {
        sendMessage(message, Formatting.GREEN);
    }

    /**
     * Sends an info message in aqua
     *
     * @param message The info message
     */
    public static void sendInfo(String message) {
        sendMessage(message, Formatting.AQUA);
    }

    /**
     * Sends a warning message in yellow
     *
     * @param message The warning message
     */
    public static void sendWarning(String message) {
        sendMessage(message, Formatting.YELLOW);
    }

    /**
     * Sends a prefixed message with custom formatting
     *
     * @param prefix            The prefix to add before the message
     * @param message           The message to send
     * @param prefixFormatting  The formatting for the prefix
     * @param messageFormatting The formatting for the message
     */
    public static void sendPrefixedMessage(String prefix, String message, Formatting prefixFormatting, Formatting messageFormatting) {
        if(mc.player == null) return;

        MutableText prefixText = Text.literal("[" + prefix + "] ").setStyle(Style.EMPTY.withColor(prefixFormatting));
        MutableText messageText = Text.literal(message).setStyle(Style.EMPTY.withColor(messageFormatting));
        mc.player.sendMessage(prefixText.append(messageText), false);
    }
}
