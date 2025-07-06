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

package rip.tenacity.module.impl.visual.clickgui;

import rip.tenacity.TenacityClient;
import rip.tenacity.config.KeybindManager;
import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUIModule extends Module {

    private final BooleanSetting showTooltips = new BooleanSetting("ShowTooltips", "Show tooltips when hovering over modules and settings", true);

    public ClickGUIModule() {
        super("ClickGUI", "Click GUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);

        KeybindManager.getInstance().bind(this, GLFW.GLFW_KEY_RIGHT_SHIFT);
        TenacityClient.getEventBus().subscribe(this);

        addSetting(showTooltips);
    }

    @Override
    protected void onEnable() {
        MinecraftClient.getInstance().setScreen(new DropDownClickGUI(showTooltips.getValue()));
        super.setEnabled(false);
    }
}