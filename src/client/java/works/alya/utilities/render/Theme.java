/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.utilities.render;

public class Theme {
    public static int COLOR$1 = 0xFF5A2F9C;
    public static int COLOR$2 = 0xFF7848C2;

    public static int getInterpolatedColors(float factor) {
        return ColorUtility.interpolateColor(
                COLOR$1,
                COLOR$2,
                factor
        );
    }
}