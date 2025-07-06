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

package works.alya.module.impl.visual.esp;

import works.alya.config.setting.impl.ModeSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.visual.esp.BoundingBox.BoundingBoxESP;
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
