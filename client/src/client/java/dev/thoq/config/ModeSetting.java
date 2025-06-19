package dev.thoq.config;

import java.util.Arrays;
import java.util.List;

/**
 * A setting that allows cycling through a list of predefined options.
 */
public class ModeSetting extends Setting<String> {
    private final List<String> modes;

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
        this.modes = Arrays.asList(modes);

        // Ensure default value is in the modes list
        if(!this.modes.contains(defaultValue)) {
            throw new IllegalArgumentException("Default value must be one of the modes");
        }
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
        setValue(modes.get(nextIndex));
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
}