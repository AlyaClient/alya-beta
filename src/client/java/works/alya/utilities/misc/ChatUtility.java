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

package works.alya.utilities.misc;

import works.alya.AlyaClient;
import works.alya.module.impl.visual.DebugModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.swing.tree.ExpandVetoException;

@SuppressWarnings("unused")
public class ChatUtility {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final String RYE = "§5Rye§r";

    /**
     * Sends a formatted message to the player
     *
     * @param message    The message to send
     * @param formatting The color/formatting to apply
     */
    public static void sendMessage(String message, Formatting formatting) {
        if(mc.player != null) {
            String newMessage = String.format("%s %s", RYE, message.replace("»", "§0»"));
            MutableText text = Text.literal(message).setStyle(Style.EMPTY.withColor(formatting));
            mc.player.sendMessage(text, false);
        }
    }

    public static void sendDebug(String message) {
        boolean debug = AlyaClient.INSTANCE.getModuleRepository().getModule(DebugModule.class).isEnabled();

        if(mc.player != null && debug) {
            MutableText text = Text.literal("Debug » " + message);
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
        sendMessage("Error » " + message, Formatting.RED);
    }

    public static void sendScriptError(Exception error) {
        sendMessage(String.format(
                "Alya Scripting: %s",
                error.getMessage() != null ? error.getMessage() : error.toString()
        ));
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
        sendMessage(message, Formatting.LIGHT_PURPLE);
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

        MutableText prefixText = Text.literal(RYE + " " + prefix + " » ").setStyle(Style.EMPTY.withColor(prefixFormatting));
        MutableText messageText = Text.literal(message).setStyle(Style.EMPTY.withColor(messageFormatting));
        mc.player.sendMessage(prefixText.append(messageText), false);
    }
}
