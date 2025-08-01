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

package works.alya.module;

import works.alya.AlyaClient;
import works.alya.config.VisualManager;
import works.alya.config.setting.Setting;
import works.alya.config.setting.impl.ModeSetting;
import net.minecraft.client.MinecraftClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class Module {
    private final String name;
    private final String displayName;
    private final String description;
    private final ModuleCategory category;
    private boolean enabled;
    private String prefix = "";
    protected final Map<String, Setting<?>> settings = new HashMap<>();
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final HashMap<String, SubModule> submodules = new HashMap<>();
    protected final ModeSetting mode = new ModeSetting("Mode", "Da mode", "");

    protected Module(String name, String displayName, String description, ModuleCategory category) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
    }

    protected Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.displayName = name;
        this.description = description;
        this.category = category;
    }

    protected void addSubmodules(final SubModule... modules) {
        this.addSetting(this.mode);

        this.mode.addCallback((Boolean before) -> {
            if(this.isEnabled()) {
                if(before) {
                    this.submodules.get(this.mode.getValue()).onDisable();
                } else {
                    this.submodules.get(this.mode.getValue()).onEnable();
                }
            }
        });

        for(final SubModule module : modules) {
            this.submodules.put(module.name, module);
            this.mode.add(module.name);

            for(final Setting<?> setting : module.settings) {
                this.settings.put(setting.getName().toLowerCase(), setting); // This setting system is so scuffed. D:
            }
        }

        this.mode.setValue(modules[0].name);
        this.mode.setDefaultValue(modules[0].name);
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

    public String getDisplayName() {
        return displayName;
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
            AlyaClient.getEventBus().subscribe(this);
            onEnable();
        } else {
            AlyaClient.getEventBus().unsubscribe(this);
            onDisable();
        }

        if(this.category == ModuleCategory.VISUAL && MinecraftClient.getInstance().player != null) {
            VisualManager.getInstance().updateVisualModuleData(this);
            VisualManager.getInstance().saveVisualData();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected <T> void addSetting(Setting<T> setting) {
        settings.put(setting.getName().toLowerCase(), setting);

        // For visual modules, add a callback to save settings when they change
        if(this.category == ModuleCategory.VISUAL) {
            setting.setChangeCallback(changedSetting -> {
                if(MinecraftClient.getInstance().player != null) {
                    VisualManager.getInstance().updateVisualModuleData(this);
                    VisualManager.getInstance().saveVisualData();
                }
            });
        }
    }

    public Setting<?> getSetting(String name) {
        return settings.get(name.toLowerCase());
    }

    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    protected void onEnable() {
        if(!this.submodules.isEmpty()) {
            System.out.println(this.submodules.toString());
            System.out.println(this.mode.getValue() + "    |    " + this.mode.getDefaultValue());
            this.submodules.get(this.mode.getValue()).onEnable();
        }
    }

    protected void onDisable() {
        if(!this.submodules.isEmpty()) {
            this.submodules.get(this.mode.getValue()).onDisable();
        }
    }

    /**
     * Sets a prefix for this module that will be displayed in the arraylist
     *
     * @param prefix The prefix to display before the module name
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    /**
     * Gets the current prefix for this module
     *
     * @return The current prefix, or empty string if none is set
     */
    public String getPrefix() {
        return prefix;
    }
}
