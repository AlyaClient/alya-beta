package dev.thoq.module;

import dev.thoq.config.Setting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Module {
    private final String name;
    private final String description;
    private boolean enabled;
    protected final Map<String, Setting<?>> settings = new HashMap<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("enabled", this.enabled);
        return state;
    }

    public void loadState(Map<String, Object> state) {
        if(state.containsKey("enabled")) {
            setEnabled((Boolean) state.get("enabled"));
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            onEnable();
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(isEnabled()) onTick();
            });
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected <T> void addSetting(Setting<T> setting) {
        settings.put(setting.getName().toLowerCase(), setting);
    }

    public Setting<?> getSetting(String name) {
        return settings.get(name.toLowerCase());
    }

    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected abstract void onTick();
}
