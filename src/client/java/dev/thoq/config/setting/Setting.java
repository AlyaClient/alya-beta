/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.config.setting;

import dev.thoq.config.VisibilityCondition;

public class Setting<T> {
    private final String name;
    private final String description;
    private T value;
    private final T defaultValue;
    private final T minValue;
    private final T maxValue;
    private VisibilityCondition visibilityCondition = () -> true; // Default to always visible

    @SuppressWarnings("unused")
    public Setting(String name, String description, T defaultValue) {
        this(name, description, defaultValue, null, null);
    }

    public Setting(String name, String description, T defaultValue, T minValue, T maxValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public void setValue(T value) {
        if(minValue != null && maxValue != null) {
            if(value instanceof Number && minValue instanceof Number && maxValue instanceof Number) {
                double val = ((Number) value).doubleValue();
                double min = ((Number) minValue).doubleValue();
                double max = ((Number) maxValue).doubleValue();

                if(val < min) val = min;
                if(val > max) val = max;

                switch(this.value) {
                    case Integer ignored -> this.value = (T) Integer.valueOf((int) val);
                    case Float ignored -> this.value = (T) Float.valueOf((float) val);
                    case Double ignored -> this.value = (T) Double.valueOf(val);
                    default -> throw new IllegalStateException("Unexpected value: " + this.value);
                }
                return;
            }
        }
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setValueFromObject(Object value) {
        if(value == null) return;

        if(this.value instanceof Boolean && value instanceof Boolean) {
            setValue((T) value);
        } else if(this.value instanceof Number) {
            if(value instanceof Number num) {
                switch(this.value) {
                    case Integer ignored -> setValue((T) Integer.valueOf(num.intValue()));
                    case Float ignored -> setValue((T) Float.valueOf(num.floatValue()));
                    case Double ignored -> setValue((T) Double.valueOf(num.doubleValue()));
                    default -> throw new IllegalStateException("Unexpected value: " + this.value);
                }
            }
        } else if(this.value instanceof String) {
            setValue((T) value.toString());
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unused")
    public T getMinValue() {
        return minValue;
    }

    @SuppressWarnings("unused")
    public T getMaxValue() {
        return maxValue;
    }

    /**
     * Gets the type of this setting.
     * @return "generic"
     */
    public String getType() {
        return "generic";
    }

    /**
     * Sets the visibility condition for this setting.
     * 
     * @param condition The condition that determines whether this setting should be visible
     * @return This setting instance for method chaining
     */
    public Setting<T> setVisibilityCondition(VisibilityCondition condition) {
        this.visibilityCondition = condition;
        return this;
    }

    /**
     * Checks if this setting should be visible based on its visibility condition.
     * 
     * @return true if the setting should be visible, false otherwise
     */
    public boolean isVisible() {
        return visibilityCondition.isVisible();
    }
}
