package dev.thoq.module.impl.client;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.ui.SettingsUI;
import net.minecraft.client.MinecraftClient;

/**
 * Module that opens the settings UI when enabled.
 */
public class SettingsModule extends Module {
    
    public SettingsModule() {
        super("Settings", "Open the client settings menu", ModuleCategory.CLIENT);
    }
    
    @Override
    protected void onEnable() {
        // Open the settings UI
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new SettingsUI());
        });
        
        // Disable the module after opening the UI
        // This prevents the module from staying enabled
        setEnabled(false);
    }
    
    @Override
    protected void onDisable() {
        // Nothing to do here
    }
    
    @Override
    protected void onTick() {
        // Nothing to do here
    }
}