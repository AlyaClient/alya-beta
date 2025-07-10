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

package works.alya.config.setting.impl;

import works.alya.config.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A setting that allows cycling through a list of predefined options.
 */
@SuppressWarnings("unused")
public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private Consumer<Boolean> changeCallback = null;

    /**
     * Creates a new mode setting.
     *
     * @param name The name of the setting
     * @param description The description of the setting
     * @param defaultValue The default value (must be one of the modes)
     * @param modes The available modes to cycle through
     */
    public ModeSetting(String name, String description, String defaultValue, String... modes) {
        super(name, description, defaultValue);
        this.modes = new ArrayList<>(Arrays.asList(modes));
    }

    /**
     * Gets all available modes.
     *
     * @return The list of available modes
     */
    public List<String> getModes() {
        return modes;
    }

    /**
     * Cycles to the next mode in the list.
     */
    public void cycle() {
        String currentValue = getValue();
        int currentIndex = modes.indexOf(currentValue);
        int nextIndex = (currentIndex + 1) % modes.size();
        if(this.changeCallback != null) this.changeCallback.accept(true);
        setValue(modes.get(nextIndex));
        if(this.changeCallback != null) this.changeCallback.accept(false);
    }

    /**
     * Gets the current mode index.
     *
     * @return The index of the current mode
     */
    public int getCurrentIndex() {
        return modes.indexOf(getValue());
    }

    /**
     * Gets the type of this setting.
     * @return "mode"
     */
    public String getType() {
        return "mode";
    }

    @Override
    public void setValue(final String value) {
        if(this.changeCallback != null) this.changeCallback.accept(true);
        super.setValue(value);
        if(this.changeCallback != null) this.changeCallback.accept(false);
    }

    public void add(final String mode) {
        this.modes.add(mode);
    }

    public void addCallback(final Consumer<Boolean> runnable) {
        this.changeCallback = runnable;
    }

}