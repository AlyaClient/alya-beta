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

package dev.thoq.module.impl.visual.clickgui;

import dev.thoq.RyeClient;
import dev.thoq.config.KeybindManager;
import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUIModule extends Module {

    private final BooleanSetting showTooltips = new BooleanSetting("ShowTooltips", "Show tooltips when hovering over modules and settings", true);

    public ClickGUIModule() {
        super("ClickGUI", "Click GUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);

        KeybindManager.getInstance().bind(this, GLFW.GLFW_KEY_RIGHT_SHIFT);
        RyeClient.getEventBus().subscribe(this);

        addSetting(showTooltips);
    }

    @Override
    protected void onEnable() {
        MinecraftClient.getInstance().setScreen(new DropDownClickGUI(showTooltips.getValue()));
        super.setEnabled(false);
    }
}