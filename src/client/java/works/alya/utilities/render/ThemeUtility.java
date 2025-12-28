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

package works.alya.utilities.render;

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
