/*
 * Copyright (c) Rye Client 2025-2025.
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

/**
 * A setting that allows inputting a specific number value.
 * @param <T> The numeric type (Integer, Float, Double)
 */
@SuppressWarnings({"unchecked", "unused"})
public class NumberSetting<T extends Number> extends Setting<T> {

    private static final double FAST_INCREMENT_FACTOR = 5.0;

    /**
     * Creates a new number setting.
     *
     * @param name The name of the setting
     * @param description The description of the setting
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     */
    public NumberSetting(String name, String description, T defaultValue, T minValue, T maxValue) {
        super(name, description, defaultValue, minValue, maxValue);
    }

    /**
     * Increments the value by a small amount based on the type.
     * For floating-point types, ensures the value has only one decimal place.
     *
     * @param fast Whether to increment faster (for held clicks)
     */
    public void increment(boolean fast) {
        T value = getValue();
        double incrementAmount = fast ? FAST_INCREMENT_FACTOR : 1.0;

        if(value instanceof Integer) {
            setValue((T) Integer.valueOf(((Integer) value) + (int)incrementAmount));
        } else if(value instanceof Float) {
            float newValue = ((Float) value) + (float)(0.1 * incrementAmount);
            newValue = Math.round(newValue * 10) / 10.0f;
            setValue((T) Float.valueOf(newValue));
        } else if(value instanceof Double) {
            double newValue = ((Double) value) + (0.1 * incrementAmount);
            newValue = Math.round(newValue * 10) / 10.0;
            setValue((T) Double.valueOf(newValue));
        }
    }

    /**
     * Increments the value by a small amount based on the type.
     * For backward compatibility.
     */
    public void increment() {
        increment(false);
    }

    /**
     * Decrements the value by a small amount based on the type.
     * For floating-point types, ensures the value has only one decimal place.
     *
     * @param fast Whether to decrement faster (for held clicks)
     */
    public void decrement(boolean fast) {
        T value = getValue();
        double decrementAmount = fast ? FAST_INCREMENT_FACTOR : 1.0;

        if(value instanceof Integer) {
            setValue((T) Integer.valueOf(((Integer) value) - (int)decrementAmount));
        } else if(value instanceof Float) {
            float newValue = ((Float) value) - (float)(0.1 * decrementAmount);
            newValue = Math.round(newValue * 10) / 10.0f;
            setValue((T) Float.valueOf(newValue));
        } else if(value instanceof Double) {
            double newValue = ((Double) value) - (0.1 * decrementAmount);
            newValue = Math.round(newValue * 10) / 10.0;
            setValue((T) Double.valueOf(newValue));
        }
    }

    /**
     * Decrements the value by a small amount based on the type.
     * For backward compatibility.
     */
    public void decrement() {
        decrement(false);
    }

    /**
     * Gets the type of this setting.
     * @return "number"
     */
    public String getType() {
        return "number";
    }
}
