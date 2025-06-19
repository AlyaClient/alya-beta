package dev.thoq.config;

/**
 * A setting that allows inputting a specific number value.
 * @param <T> The numeric type (Integer, Float, Double)
 */
@SuppressWarnings("unchecked")
public class NumberSetting<T extends Number> extends Setting<T> {

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
     */
    public void increment() {
        T value = getValue();
        if(value instanceof Integer) {
            setValue((T) Integer.valueOf(((Integer) value) + 1));
        } else if(value instanceof Float) {
            setValue((T) Float.valueOf(((Float) value) + 0.1f));
        } else if(value instanceof Double) {
            setValue((T) Double.valueOf(((Double) value) + 0.1));
        }
    }

    /**
     * Decrements the value by a small amount based on the type.
     */
    public void decrement() {
        T value = getValue();
        if(value instanceof Integer) {
            setValue((T) Integer.valueOf(((Integer) value) - 1));
        } else if(value instanceof Float) {
            setValue((T) Float.valueOf(((Float) value) - 0.1f));
        } else if(value instanceof Double) {
            setValue((T) Double.valueOf(((Double) value) - 0.1));
        }
    }

    /**
     * Gets the type of this setting.
     * @return "number"
     */
    public String getType() {
        return "number";
    }
}