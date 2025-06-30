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
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import org.lwjgl.glfw.GLFW;

public class ClickGUIModule extends Module {

    private final ModeSetting guiMode = new ModeSetting("GUIMode", "Choose between dropdown and window GUI", "Dropdown", "Dropdown");
    private final DropDownClickGUI dropDownGUI = new DropDownClickGUI();

    public ClickGUIModule() {
        super("ClickGUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);

        KeybindManager.getInstance().bind(this, GLFW.GLFW_KEY_RIGHT_SHIFT);
        RyeClient.getEventBus().subscribe(this);
        addSetting(guiMode);
    }

    @Override
    protected void onEnable() {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.show();
        }
    }

    @Override
    protected void onDisable() {
        dropDownGUI.hide();
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.tick();
        }
    };

    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if(guiMode.getValue().equals("Dropdown")) {
            dropDownGUI.render(event.getContext());
        }
    };
}