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

import java.awt.*;

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
            case GRAY -> 0xFF808080;
            case LIGHT_GRAY -> 0xFFD3D3D3;
            case PANEL -> 0x60000000;
        };
    }

    public static int getIntFromColor(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
