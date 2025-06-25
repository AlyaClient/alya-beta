/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.DrawContext;

public class FPSModule extends Module {
    public FPSModule() {
        super("FPS", "Shows current game fps", ModuleCategory.VISUAL);
        this.setEnabled(true);
    }

    @Override
    protected void onRender(DrawContext context) {
        String fps = String.format(
                "[%sFPS%s] %s",
                "§d",
                "§r",
                RyeClient.getFps()
        );

        TextRendererUtility.renderText(
                context,
                fps,
                ColorUtility.Colors.LIGHT_GRAY,
                1,
                17,
                true
        );
    }
}
