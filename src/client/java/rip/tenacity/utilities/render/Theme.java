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

public class Theme {
    public static int COLOR$1 = 0xEE2DB9F0;
    public static int COLOR$2 = 0xEEF383D7;

    public static int getInterpolatedColors(float factor) {
        return ColorUtility.interpolateColor(
                COLOR$1,
                COLOR$2,
                factor
        );
    }
}