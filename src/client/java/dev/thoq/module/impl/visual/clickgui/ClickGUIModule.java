package dev.thoq.module.impl.visual.clickgui;

import dev.thoq.RyeClient;
import dev.thoq.config.KeybindManager;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.visual.clickgui.dropdown.DropDownClickGUI;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUIModule extends Module {

    private final ModeSetting guiMode = new ModeSetting("GUIMode", "Choose between dropdown and window GUI", "Dropdown", "Dropdown");

    public ClickGUIModule() {
        super("ClickGUI", "Toggle modules with a graphical interface", ModuleCategory.VISUAL);

        KeybindManager.getInstance().bind(this, GLFW.GLFW_KEY_RIGHT_SHIFT);
        RyeClient.getEventBus().subscribe(this);
        addSetting(guiMode);
    }

    @Override
    protected void onEnable() {
        if(guiMode.getValue().equals("Dropdown")) {
            MinecraftClient.getInstance().setScreen(new DropDownClickGUI());
        }
    }
}