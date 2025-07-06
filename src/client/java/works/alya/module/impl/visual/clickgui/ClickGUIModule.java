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

package works.alya.module.impl.visual.clickgui;

import works.alya.AlyaClient;
import works.alya.config.KeybindManager;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUIModule extends Module {

    private final BooleanSetting showTooltips = new BooleanSetting("ShowTooltips", "Show tooltips when hovering over modules and settings", true);

    public ClickGUIModule() {
        super("ClickGUI", "Click GUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);

        KeybindManager.getInstance().bind(this, GLFW.GLFW_KEY_RIGHT_SHIFT);
        AlyaClient.getEventBus().subscribe(this);

        addSetting(showTooltips);
    }

    @Override
    protected void onEnable() {
        MinecraftClient.getInstance().setScreen(new DropDownClickGUI(showTooltips.getValue()));
        super.setEnabled(false);
    }
}