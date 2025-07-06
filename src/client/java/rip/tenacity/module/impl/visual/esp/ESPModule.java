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

package rip.tenacity.module.impl.visual.esp;

import rip.tenacity.config.setting.impl.ModeSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.Render2DEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.visual.esp.BoundingBox.BoundingBoxESP;
import net.minecraft.client.MinecraftClient;

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

        setPrefix(mode);

        switch(mode) {
            case "2D": {
                BoundingBoxESP.render(event.getContext(), mc);
                break;
            }
        }
    };
}
