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

package dev.thoq.config.setting.impl;

import dev.thoq.config.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A setting that allows selecting multiple boolean options from a predefined list.
 */
public class MultipleBooleanSetting extends Setting<Map<String, Boolean>> {
    private final List<String> options;
    private boolean expanded = false;

    /**
     * Creates a new multiple boolean setting.
     *
     * @param name The name of the setting
     * @param description The description of the setting
     * @param options The available options to select from
     */
    public MultipleBooleanSetting(String name, String description, String... options) {
        super(name, description, createDefaultMap(options));
        this.options = Arrays.asList(options);
    }

    /**
     * Creates a default map with all options set to false.
     *
     * @param options The available options
     * @return A map with all options set to false
     */
    private static Map<String, Boolean> createDefaultMap(String[] options) {
        Map<String, Boolean> defaultMap = new HashMap<>();
        for (String option : options) {
            defaultMap.put(option, false);
        }
        return defaultMap;
    }

    /**
     * Gets all available options.
     *
     * @return The list of available options
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Toggles the value of a specific option.
     *
     * @param option The option to toggle
     */
    public void toggle(String option) {
        if (options.contains(option)) {
            Map<String, Boolean> currentValue = getValue();
            currentValue.put(option, !currentValue.get(option));
            setValue(currentValue);
        }
    }

    /**
     * Checks if a specific option is enabled.
     *
     * @param option The option to check
     * @return True if the option is enabled, false otherwise
     */
    public boolean isEnabled(String option) {
        return getValue().getOrDefault(option, false);
    }

    /**
     * Gets a list of all enabled options.
     *
     * @return A list of enabled options
     */
    public List<String> getEnabledOptions() {
        List<String> enabledOptions = new ArrayList<>();
        Map<String, Boolean> currentValue = getValue();
        
        for (String option : options) {
            if (currentValue.getOrDefault(option, false)) {
                enabledOptions.add(option);
            }
        }
        
        return enabledOptions;
    }

    /**
     * Toggles the expanded state of the setting.
     */
    public void toggleExpanded() {
        expanded = !expanded;
    }

    /**
     * Checks if the setting is expanded.
     *
     * @return True if the setting is expanded, false otherwise
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Gets the type of this setting.
     * @return "multipleboolean"
     */
    public String getType() {
        return "multipleboolean";
    }
}