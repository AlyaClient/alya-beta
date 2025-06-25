/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.config.setting.impl;

import dev.thoq.config.setting.Setting;

/**
 * A setting that represents a boolean toggle switch.
 */
public class BooleanSetting extends Setting<Boolean> {

    /**
     * Creates a new boolean setting.
     *
     * @param name The name of the setting
     * @param description The description of the setting
     * @param defaultValue The default value
     */
    public BooleanSetting(String name, String description, Boolean defaultValue) {
        super(name, description, defaultValue);
    }

    /**
     * Toggles the boolean value.
     */
    public void toggle() {
        setValue(!getValue());
    }

    /**
     * Gets the type of this setting.
     * @return "boolean"
     */
    public String getType() {
        return "boolean";
    }
}