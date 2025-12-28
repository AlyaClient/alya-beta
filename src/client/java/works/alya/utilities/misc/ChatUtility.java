/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
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

@SuppressWarnings("unused")
public final class ChatUtility {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final String RYE = "§5Alya§r";

    /**
     * Sends a formatted message to the player
     *
     * @param message    The message to send
     * @param formatting The color/formatting to apply
     */
    public static void sendMessage(final String message, final Formatting formatting) {
        if(mc.player != null) {
            final String newMessage = String.format("%s %s", RYE, message.replace("»", "§0»"));
            final MutableText text = Text.literal(message).setStyle(Style.EMPTY.withColor(formatting));
            mc.player.sendMessage(text, false);
        }
    }

    public static void sendDebug(final String message) {
        final boolean debug = AlyaClient.INSTANCE.getModuleRepository().getModule(DebugModule.class).isEnabled();

        if(mc.player != null && debug) {
            final MutableText text = Text.literal("Debug » " + message);
            mc.player.sendMessage(text, false);
        }
    }

    /**
     * Sends a regular message to chat
     *
     * @param message The message to send
     */
    public static void sendMessage(final String message) {
        sendMessage(message, Formatting.WHITE);
    }

    /**
     * Sends an error message in red
     *
     * @param message The error message
     */
    public static void sendError(final String message) {
        sendMessage("Error » " + message, Formatting.RED);
    }

    public static void sendScriptError(final Exception error) {
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
    public static void sendSuccess(final String message) {
        sendMessage(message, Formatting.GREEN);
    }

    /**
     * Sends an info message in aqua
     *
     * @param message The info message
     */
    public static void sendInfo(final String message) {
        sendMessage(message, Formatting.LIGHT_PURPLE);
    }

    /**
     * Sends a warning message in yellow
     *
     * @param message The warning message
     */
    public static void sendWarning(final String message) {
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
    public static void sendPrefixedMessage(final String prefix, final String message, final Formatting prefixFormatting, final Formatting messageFormatting) {
        if(mc.player == null) return;

        final MutableText prefixText = Text.literal(RYE + " " + prefix + " » ").setStyle(Style.EMPTY.withColor(prefixFormatting));
        final MutableText messageText = Text.literal(message).setStyle(Style.EMPTY.withColor(messageFormatting));
        mc.player.sendMessage(prefixText.append(messageText), false);
    }
}
