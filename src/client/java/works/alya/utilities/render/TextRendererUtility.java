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

package works.alya.utilities.render;

import works.alya.font.FontManager;
import works.alya.utilities.render.ColorUtility.Colors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class TextRendererUtility {

    static TextRenderer renderer = FontManager.getFont("sf_pro_rounded_regular", 11);
    static TextRenderer rendererMd = FontManager.getFont("sf_pro_rounded_regular", 15);
    static TextRenderer rendererXl = FontManager.getFont("sf_pro_rounded_regular", 40);

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
     * Renders an extra-large text string on the screen at the specified position with a designated color and optional shadow.
     *
     * @param context The drawing context used for rendering.
     * @param text The text to be rendered.
     * @param color The color of the text, specified as an enum value from {@link Colors}.
     * @param posX The X-coordinate where the text should be rendered.
     * @param posY The Y-coordinate where the text should be rendered.
     * @param shadow A boolean value indicating whether to render the text with a shadow effect.
     */
    public static void renderXlText(
            DrawContext context,
            String text,
            Colors color,
            int posX,
            int posY,
            boolean shadow
    ) {
        context.drawText(
                rendererXl,
                text,
                posX,
                posY,
                ColorUtility.getColor(color),
                shadow
        );
    }

    /**
     * Renders a medium (Md) text string on the screen at the specified position with a designated color and optional shadow.
     *
     * @param context The drawing context used for rendering the text.
     * @param text The text to be rendered.
     * @param color The color of the text, specified as an enum value from {@link Colors}.
     * @param posX The X-coordinate where the text should be rendered.
     * @param posY The Y-coordinate where the text should be rendered.
     * @param shadow A boolean value indicating whether to render the text with a shadow effect.
     */
    public static void renderMdText(
            DrawContext context,
            String text,
            Colors color,
            int posX,
            int posY,
            boolean shadow
    ) {
        context.drawText(
                rendererMd,
                text,
                posX,
                posY,
                ColorUtility.getColor(color),
                shadow
        );
    }

    /**
     * Renders a font based on input-will have worse performance
     * @param context The drawing context used for rendering the text.
     * @param text The text to be rendered.
     * @param color The color of the text, specified as an enum value from {@link Colors}.
     * @param posX The X-coordinate where the text should be rendered.
     * @param posY The Y-coordinate where the text should be rendered.
     * @param shadow A boolean value indicating whether to render the text with a shadow effect.
     */
    public static void renderDynamicText(
            DrawContext context,
            String text,
            Colors color,
            int posX,
            int posY,
            boolean shadow,
            String fontName,
            int size
    ) {
        TextRenderer dynamicRenderer = FontManager.getFont(fontName, size);
        context.drawText(
                dynamicRenderer,
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
     * Calculates the width of the given extra-large text string using the renderer.
     *
     * @param text The text whose width is to be measured.
     * @return The width of the text in pixels.
     */
    public static int getXlTextWidth(String text) {
        return rendererXl.getWidth(text);
    }

    /**
     * Retrieves the height of the text font used by the Minecraft text renderer.
     *
     * @return The height of the font in pixels.
     */
    public static int getTextHeight() {
        return renderer.fontHeight;
    }

    /**
     * Retrieves the height of the extra-large text font used by the renderer.
     *
     * @return The height of the extra-large font in pixels.
     */
    public static int getXlTextHeight() {
        return rendererXl.fontHeight;
    }

    /**
     * Retrieves the height of the medium (Md) text font used by the renderer.
     *
     * @return The height of the medium font in pixels.
     */
    public static int getMdTextHeight() {
        return rendererMd.fontHeight;
    }
}
