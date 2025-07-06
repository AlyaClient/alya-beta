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

package works.alya.utilities.misc;

import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.Theme;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;

public class BackgroundUtility {
    public static void drawBackground(DrawContext context) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        RenderUtility.drawGradientRoundedRect(
                context,
                0,
                0,
                width,
                height,
                new Vector4f(0, 0, 0, 0),
                Theme.COLOR$1,
                Theme.COLOR$2
        );
    }
}