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

public class Theme {
    public static int COLOR$1 = 0xDDFF00B3;
    public static int COLOR$2 = 0xDDFF8800;

    public static int getInterpolatedColors(float factor) {
        return ColorUtility.interpolateColor(
                COLOR$1,
                COLOR$2,
                factor
        );
    }
}