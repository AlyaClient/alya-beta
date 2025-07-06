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