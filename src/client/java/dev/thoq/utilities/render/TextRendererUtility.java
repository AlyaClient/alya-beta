/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.utilities.render;

import dev.thoq.font.FontManager;
import dev.thoq.utilities.render.ColorUtility.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class TextRendererUtility {

    static MinecraftClient client = MinecraftClient.getInstance();
    static TextRenderer renderer = FontManager.getFont("figtree", 11);

    /**
     * Renders a text string on the screen at the specified position with a designated color and optional shadow.
     *
     * @param context The drawing context used for rendering.
     * @param text The text to be rendered.
     * @param color The color of the text, specified as an enum value from {@link Colors}.
     * @param posX The X-coordinate where the text should be rendered.
     * @param posY The Y-coordinate where the text should be rendered.
     * @param shadow A boolean value indicating whether to render the text with a shadow effect.
     */
    public static void renderText(
            DrawContext context,
            String text,
            Colors color,
            int posX,
            int posY,
            boolean shadow
    ) {
        context.drawText(
                renderer,
                text,
                posX,
                posY,
                ColorUtility.getColor(color),
                shadow
        );
    }

    /**
     * Renders a text string on the screen at the specified position with a designated color and optional shadow.
     *
     * @param context The drawing context used for rendering.
     * @param text The text to be rendered.
     * @param color The color of the text, specified as an ARGB integer value.
     * @param posX The X-coordinate where the text should be rendered.
     * @param posY The Y-coordinate where the text should be rendered.
     * @param shadow A boolean value indicating whether to render the text with a shadow effect.
     */
    public static void renderText(
            DrawContext context,
            String text,
            int color,
            int posX,
            int posY,
            boolean shadow
    ) {
        context.drawText(
                renderer,
                text,
                posX,
                posY,
                color,
                shadow
        );
    }

    /**
     * Gets the width of the given text.
     *
     * @param text The text to measure
     * @return The width of the text in pixels
     */
    public static int getTextWidth(String text) {
        return renderer.getWidth(text);
    }

    /**
     * Retrieves the height of the text font used by the Minecraft text renderer.
     *
     * @return The height of the font in pixels.
     */
    public static int getTextHeight() {
        return renderer.fontHeight;
    }
}
