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
public class ThemeUtility {
    private static Color themeColorFirst = Color.PINK;
    private static Color themeColorSecond = Color.BLUE;

    public static Color getThemeColorFirst() {
        return themeColorFirst;
    }

    public static Color getThemeColorSecond() {
        return themeColorSecond;
    }

    public static void setThemeColorFirst(Color color) {
        themeColorFirst = color;
    }

    public static void setThemeColorSecond(Color color) {
        themeColorSecond = color;
    }
}
