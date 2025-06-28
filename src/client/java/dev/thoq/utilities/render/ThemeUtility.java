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

import java.awt.*;

@SuppressWarnings("unused")
public class ThemeUtility {
    private static Color themeColorFirst = Color.PINK;
    private static Color themeColorSecond = Color.GREEN;

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
