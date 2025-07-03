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

package dev.thoq.module;

import dev.thoq.RyeClient;
import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.ModeSetting;
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
            if(before) {
                this.submodules.get(this.mode.getValue()).onDisable();
            } else {
                this.submodules.get(this.mode.getValue()).onEnable();
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
            RyeClient.getEventBus().subscribe(this);
            onEnable();
        } else {
            RyeClient.getEventBus().unsubscribe(this);
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
}
