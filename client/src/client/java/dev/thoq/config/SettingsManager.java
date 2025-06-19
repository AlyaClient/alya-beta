package dev.thoq.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.thoq.RyeClient;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages global client settings that are not tied to specific modules.
 */
public class SettingsManager {
    private static final SettingsManager INSTANCE = new SettingsManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<Setting<?>> settings = new ArrayList<>();
    private final BooleanSetting useDefaultFont;
    private boolean initialized = false;

    private SettingsManager() {
        useDefaultFont = new BooleanSetting("Use Default Font", "Use Minecraft's default font instead of custom font", false);

        settings.add(useDefaultFont);
    }

    /**
     * Initializes the settings manager by loading settings from file.
     * This should be called after Minecraft is fully initialized.
     */
    public void initialize() {
        if (!initialized) {
            loadSettings();
            initialized = true;
        }
    }

    /**
     * Gets the settings file path.
     *
     * @return The settings file path
     */
    private String getSettingsFilePath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.runDirectory == null) {
            return "settings.json";
        }
        return client.runDirectory + "/" + RyeClient.getName() + "/settings.json";
    }

    public static SettingsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Gets all global settings.
     *
     * @return List of all settings
     */
    public List<Setting<?>> getSettings() {
        return settings;
    }

    /**
     * Gets a setting by name.
     *
     * @param name The name of the setting
     * @return The setting, or null if not found
     */
    public Setting<?> getSetting(String name) {
        for (Setting<?> setting : settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }
        return null;
    }

    /**
     * Gets the setting for using default Minecraft font.
     *
     * @return The setting for using default font
     */
    public BooleanSetting getUseDefaultFont() {
        return useDefaultFont;
    }

    /**
     * Saves all settings to file.
     */
    public void saveSettings() {
        Map<String, Object> settingsMap = new LinkedHashMap<>();

        for (Setting<?> setting : settings) {
            settingsMap.put(setting.getName(), setting.getValue());
        }

        File file = new File(getSettingsFilePath());

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (Writer writer = new FileWriter(file)) {
            String json = GSON.toJson(settingsMap);
            writer.write(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads all settings from file.
     */
    public void loadSettings() {
        File file = new File(getSettingsFilePath());
        if (!file.exists()) {
            saveSettings();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> settingsMap = GSON.fromJson(reader, type);

            if (settingsMap != null) {
                for (Setting<?> setting : settings) {
                    Object value = settingsMap.get(setting.getName());
                    if (value != null) {
                        setting.setValueFromObject(value);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
