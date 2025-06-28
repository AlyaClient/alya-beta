/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.utilities.render;

import dev.thoq.utilities.render.ColorUtility.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class TextRendererUtility {
    static MinecraftClient client = MinecraftClient.getInstance();

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
        TextRenderer renderer = client.textRenderer;
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
     * Gets the width of the given text.
     *
     * @param text The text to measure
     * @return The width of the text in pixels
     */
    public static int getTextWidth(String text) {
        TextRenderer renderer = client.textRenderer;
        return renderer.getWidth(text);
    }
}
