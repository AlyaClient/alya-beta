package dev.thoq.module;

import dev.thoq.config.setting.Setting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Module {
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private boolean enabled;
    protected final Map<String, Setting<?>> settings = new HashMap<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
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

    public ModuleCategory getCategory() {
        return category;
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

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onTick() {
    }

    /**
     * Called at the start of a world tick, before any game processing occurs.
     * Override this method for operations that need to be performed before the main game tick.
     * Ideal for setting up states or performing actions that should happen before the game processes the tick.
     */
    protected void onPreTick() {
    }

    /**
     * Called after the main tick processing is complete.
     * Override this method for operations that need to be performed after all other tick processing.
     * Useful for cleanup operations or for finalizing state changes that should happen at the end of a tick.
     */
    protected void onPostTick() {
    }

    /**
     * Called during the rendering process of the In-Game HUD.
     * This method is invoked for each enabled module when the HUD is rendered.
     * Override this method to implement custom rendering logic for the module,
     * such as drawing visual elements or custom overlays.
     */
    protected void onRender(DrawContext context) {
    }

    /**
     * Public accessor for onPreTick - used by mixins
     */
    public void preTick() {
        onPreTick();
    }

    /**
     * Public accessor for onPostTick - used by mixins
     */
    public void postTick() {
        onPostTick();
    }

    /**
     * Triggers the rendering process for the module by calling the {@code onRender} method.
     * This method is responsible for invoking rendering-specific logic that is implemented
     * in the module's {@code onRender} method.
     */
    public void render(DrawContext context) {
        onRender(context);
    }
}
