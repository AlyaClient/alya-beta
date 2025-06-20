package dev.thoq.config.setting.impl;

import dev.thoq.config.setting.Setting;

/**
 * A setting that allows adjusting a value using a slider.
 * @param <T> The numeric type (Integer, Float, Double)
 */
public class SliderSetting<T extends Number> extends Setting<T> {

    /**
     * Creates a new slider setting.
     *
     * @param name The name of the setting
     * @param description The description of the setting
     * @param defaultValue The default value
     * @param minValue The minimum allowed value
     * @param maxValue The maximum allowed value
     */
    public SliderSetting(String name, String description, T defaultValue, T minValue, T maxValue) {
        super(name, description, defaultValue, minValue, maxValue);
    }

    /**
     * Sets the value based on a normalized position (0.0 to 1.0) on the slider.
     *
     * @param normalizedValue A value between 0.0 and 1.0 representing the position on the slider
     */
    @SuppressWarnings("unchecked")
    public void setFromNormalizedValue(double normalizedValue) {
        // Clamp normalizedValue between 0 and 1
        normalizedValue = Math.max(0.0, Math.min(1.0, normalizedValue));

        T minValue = getMinValue();
        T maxValue = getMaxValue();

        if(minValue == null || maxValue == null) {
            return;
        }

        double min = minValue.doubleValue();
        double max = maxValue.doubleValue();
        double range = max - min;
        double value = min + (range * normalizedValue);

        if(getValue() instanceof Integer) {
            setValue((T) Integer.valueOf((int) value));
        } else if(getValue() instanceof Float) {
            setValue((T) Float.valueOf((float) value));
        } else if(getValue() instanceof Double) {
            setValue((T) Double.valueOf(value));
        }
    }

    /**
     * Gets the normalized value (0.0 to 1.0) of the current setting.
     *
     * @return A value between 0.0 and 1.0 representing the position on the slider
     */
    public double getNormalizedValue() {
        T minValue = getMinValue();
        T maxValue = getMaxValue();

        if(minValue == null || maxValue == null) {
            return 0.0;
        }

        double min = minValue.doubleValue();
        double max = maxValue.doubleValue();
        double value = getValue().doubleValue();
        double range = max - min;

        if(range == 0) {
            return 0.0;
        }

        return (value - min) / range;
    }

    /**
     * Gets the type of this setting.
     * @return "slider"
     */
    public String getType() {
        return "slider";
    }
}