/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.module.impl.visual.esp;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.visual.esp.BoundingBox.BoundingBoxESP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class ESPModule extends Module {
    private static final ModeSetting modeSetting = new ModeSetting("Mode", "Kind of ESP to use", "2D", "2D");

    public ESPModule() {
        super("ESP", "Highlights players through walls", ModuleCategory.VISUAL);

        addSetting(modeSetting);
    }

    private final IEventListener<Render2DEvent> renderEvent = event -> {
        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        MinecraftClient mc = MinecraftClient.getInstance();

        switch(mode) {
            case "2D": {
                BoundingBoxESP.render(event.getContext(), mc);
                break;
            }
        }
    };
}
