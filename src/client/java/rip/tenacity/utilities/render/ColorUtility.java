/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.utilities.render;

import java.awt.*;

@SuppressWarnings("unused")
public class ColorUtility {

    /**
     * Enum representing a collection of predefined colors. These colors can be used
     * in various contexts, such as rendering text or graphics, and are associated
     * with specific ARGB values.
     * Each enum value corresponds to a specific color:
     * - WHITE
     * - RED
     * - GREEN
     * - BLUE
     * - YELLOW
     * - CYAN
     * - MAGENTA
     * - PURPLE
     * - LAVENDER
     * - DARK_PURPLE
     * - BLACK
     * - GRAY
     * - LIGHT_GRAY
     * - PANEL
     */
    public enum Colors {
        WHITE,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        CYAN,
        MAGENTA,
        PURPLE,
        LAVENDER,
        DARK_PURPLE,
        BLACK,
        GRAY,
        LIGHT_GRAY,
        PANEL,
    }

    /**
     * Returns the ARGB color value corresponding to the specified color.
     *
     * @param color The color for which the ARGB value is to be returned, specified as an enum value of {@code Colors}.
     * @return The ARGB color value as an integer.
     */
    public static int getColor(Colors color) {
        return switch(color) {
            case WHITE -> 0xFFFFFFFF;
            case RED -> 0xFFFF0000;
            case GREEN -> 0xFF00FF00;
            case BLUE -> 0xFF0000FF;
            case YELLOW -> 0xFFFFFF00;
            case CYAN -> 0xFF00FFFF;
            case MAGENTA -> 0xFFFF00FF;
            case PURPLE -> 0x9C27F5;
            case LAVENDER -> 0xE5B8FF;
            case DARK_PURPLE -> 0x7B1FA2;
            case BLACK -> 0xFF000000;
            case GRAY -> 0xFF212121;
            case LIGHT_GRAY -> 0xFFD3D3D3;
            case PANEL -> 0x60000000;
        };
    }

    /**
     * Converts a {@link Color} object to its integer representation in ARGB format.
     * The resulting integer encodes the alpha, red, green, and blue components of the color.
     *
     * @param color The {@link Color} object to convert. This object contains the red, green, blue, and alpha components.
     * @return The integer representation of the color in ARGB format, where the 4 bytes
     *         represent alpha (highest byte), red, green, and blue (lowest byte), respectively.
     */
    public static int getIntFromColor(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return (red << 16) | (green << 8) | blue;
    }

    /**
     * Applies alpha transparency to a color
     * @param color The base color
     * @param alpha Alpha value (0-255)
     * @return Color with applied alpha
     */
    public static int applyAlpha(int color, int alpha) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Interpolates between two colors
     * @param color1 First color
     * @param color2 Second color
     * @param factor Interpolation factor (0.0 to 1.0)
     * @return Interpolated color
     */
    public static int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + factor * (a2 - a1));
        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Gets a rainbow color based on time for animated effects
     * @param time Time factor for animation
     * @param saturation Color saturation (0.0 to 1.0)
     * @param brightness Color brightness (0.0 to 1.0)
     * @return Rainbow color as integer
     */
    public static int getRainbowColor(float time, float saturation, float brightness) {
        float hue = (time % 1.0f);
        Color color = Color.getHSBColor(hue, saturation, brightness);
        return getIntFromColor(color);
    }

    /**
     * Converts an integer representation of a color in ARGB format into a {@link Color} object.
     *
     * @param color The integer representation of the color, where the 4 bytes represent alpha, red, green, and blue channels (in ARGB order).
     * @return A {@link Color} object corresponding to the given integer color.
     */
    public static Color getColorFromInt(int color) {
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return new Color(red, green, blue, alpha);
    }
}