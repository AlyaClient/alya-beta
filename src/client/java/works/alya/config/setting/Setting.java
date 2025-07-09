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

package works.alya.config.setting;

import works.alya.config.VisibilityCondition;
import java.util.function.Consumer;

public class Setting<T> {
    private final String name;
    private final String description;
    private T value;
    private T defaultValue;
    private final T minValue;
    private final T maxValue;
    private VisibilityCondition visibilityCondition = () -> true;
    private Consumer<Setting<T>> changeCallback = null;

    @SuppressWarnings("unused")
    public Setting(String name, String description, T defaultValue) {
        this(name, description, defaultValue, null, null);
    }

    public Setting(String name, String description, T defaultValue, T minValue, T maxValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
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

    public void setDefaultValue(final T value) {
        this.defaultValue = value;
    }

    public void setValue(T value) {
        if(this.minValue != null && this.maxValue != null) {
            if(value instanceof Number numValue) {
                Number numMin = (Number) this.minValue;
                Number numMax = (Number) this.maxValue;

                if(numValue.doubleValue() < numMin.doubleValue()) {
                    value = this.minValue;
                } else if(numValue.doubleValue() > numMax.doubleValue()) {
                    value = this.maxValue;
                }
            }
        }

        this.value = value;

        if(this.changeCallback != null) {
            this.changeCallback.accept(this);
        }
    }

    /**
     * Sets a callback to be called when the setting value changes.
     *
     * @param callback The callback to call when the value changes
     */
    public void setChangeCallback(Consumer<Setting<T>> callback) {
        this.changeCallback = callback;
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
     *
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
