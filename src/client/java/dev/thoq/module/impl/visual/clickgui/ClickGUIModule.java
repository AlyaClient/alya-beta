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

package dev.thoq.module.impl.visual.clickgui;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import dev.thoq.module.impl.visual.clickgui.window.WindowClickGUI;
import net.minecraft.client.gui.DrawContext;

public class ClickGUIModule extends Module {

    private final ModeSetting guiMode = new ModeSetting("GUI Mode", "Choose between dropdown and window GUI", "Dropdown", "Dropdown", "Window");
    private final DropDownClickGUI dropDownGUI;
    private final WindowClickGUI windowGUI;

    public ClickGUIModule() {
        super("ClickGUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);
        addSetting(guiMode);

        dropDownGUI = new DropDownClickGUI();
        windowGUI = new WindowClickGUI();
    }

    @Override
    protected void onEnable() {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.show();
        } else {
            windowGUI.show();
        }
    }

    @Override
    protected void onDisable() {
        dropDownGUI.hide();
        windowGUI.hide();
    }

    @Override
    protected void onPreTick() {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.tick();
        } else {
            windowGUI.tick();
        }
    }

    @Override
    protected void onRender(DrawContext context) {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.render(context);
        } else {
            windowGUI.render(context);
        }
    }
}