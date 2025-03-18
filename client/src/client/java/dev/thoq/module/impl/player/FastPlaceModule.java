package dev.thoq.module.impl.player;

import dev.thoq.module.Module;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;

@SuppressWarnings("CallToPrintStackTrace")
public class FastPlaceModule extends Module {
    private Field itemUseCooldownField;

    public FastPlaceModule() {
        super("fastplace", "helicopter helicopter");
        try {
            itemUseCooldownField = MinecraftClient.class.getDeclaredField("itemUseCooldown");
            itemUseCooldownField.setAccessible(true);
        } catch(NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null || itemUseCooldownField == null) return;

        try {
            itemUseCooldownField.set(mc, 0);
        } catch(IllegalAccessException ex) {
            ChatUtility.sendError("Failed to set block place cooldown!");
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if(mc.player == null || itemUseCooldownField == null) return;
        try {
            itemUseCooldownField.set(mc, 4);
        } catch(IllegalAccessException ex) {
            ChatUtility.sendError("Failed to reset block place cooldown!");
        }
    }
}