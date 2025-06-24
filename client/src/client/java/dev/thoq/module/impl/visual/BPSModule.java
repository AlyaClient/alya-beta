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

public class BPSModule extends Module {
    public BPSModule() {
        super("BPS", "Shows current game fps", ModuleCategory.VISUAL);
        this.setEnabled(true);
    }

    @Override
    protected void onRender(DrawContext context) {
        int x = 1;
        int y = 17;

        if(RyeClient.INSTANCE.getModuleRepository().getModuleByName("FPS").isEnabled()) y = 27;

        String fps = String.format(
                "[%sBPS%s] %s",
                "§d",
                "§r",
                RyeClient.getBps()
        );

        TextRendererUtility.renderText(
                context,
                fps,
                ColorUtility.Colors.LIGHT_GRAY,
                x,
                y,
                true
        );
    }
}
