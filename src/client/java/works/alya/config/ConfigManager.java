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

package works.alya.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import works.alya.AlyaClient;
import works.alya.config.setting.Setting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static works.alya.AlyaClient.LOGGER;

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public class ConfigManager {
    private static final String CONFIG_FOLDER = MinecraftClient.getInstance().runDirectory + "/" + AlyaClient.getName() + "/configs/";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        File folder = new File(CONFIG_FOLDER);
        if(!folder.exists()) {
            boolean result = folder.mkdirs();
            if(!result) {
                LOGGER.info("Bwoah! Failed to create config folder!");
            }
        }
    }

    /**
     * Saves the current configuration state of all modules, including their settings
     * and keybinds, to a JSON file specified by the provided name.
     * The configuration data includes whether the module is enabled, all associated
     * module settings with their respective values and types, and any defined keybinds.
     * If the file does not exist, it is created, and if it does exist, it is overwritten.
     *
     * @param name The name of the configuration file to save (excluding the file extension).
     */
    public static void saveConfig(String name) {
        Map<String, Object> config = new LinkedHashMap<>();
        Map<String, Object> modulesConfig = new LinkedHashMap<>();
        Map<String, Integer> keybinds = new LinkedHashMap<>();

        for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
            // Skip visual modules (except for enabled state) as they are handled by VisualManager
            if (module.getCategory() == ModuleCategory.VISUAL) {
                Map<String, Object> moduleConfig = new LinkedHashMap<>();
                moduleConfig.put("enabled", module.isEnabled());
                modulesConfig.put(module.getName(), moduleConfig);
                continue;
            }

            switch(module.getName().toLowerCase()) {
                // these are user preference so do not save them
                case "clickgui",
                     "ambience",
                     "discordrpc",
                     "keystrokes",
                     "menu",
                     "performance",
                     "arraylist" -> {
                    continue;
                }
            }

            Map<String, Object> moduleConfig = new LinkedHashMap<>();

            moduleConfig.put("enabled", module.isEnabled());

            Map<String, Object> settings = new LinkedHashMap<>();
            for(Setting<?> setting : module.getSettings()) {
                Map<String, Object> settingData = new LinkedHashMap<>();
                settingData.put("value", setting.getValue());
                settingData.put("type", setting.getType());

                if(setting instanceof NumberSetting<?> numberSetting) {
                    settingData.put("minValue", numberSetting.getMinValue());
                    settingData.put("maxValue", numberSetting.getMaxValue());
                } else if(setting instanceof ModeSetting modeSetting) {
                    settingData.put("modes", modeSetting.getModes());
                }

                settings.put(setting.getName(), settingData);
            }
            moduleConfig.put("settings", settings);

            modulesConfig.put(module.getName(), moduleConfig);

            Integer key = KeybindManager.getInstance().getKeyForModule(module);
            if(key != null) {
                keybinds.put(module.getName(), key);
            }
        }

        config.put("modules", modulesConfig);
        config.put("keybinds", keybinds);

        File configFile = new File(CONFIG_FOLDER + name + ".json");
        try(Writer writer = new FileWriter(configFile)) {
            String json = GSON.toJson(config);
            writer.write(json);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads a configuration file by its name, applies the settings to the respective
     * modules and keybinds, and updates the client state. If the configuration file does
     * not exist, an error message is displayed in chat.
     *
     * @param name The name of the configuration file to load, excluding the file extension.
     */
    public static void loadConfig(String name) {
        File configFile = new File(CONFIG_FOLDER + name + ".json");
        if(!configFile.exists()) {
            ChatUtility.sendError("Config doesnt exist!");
            return;
        }

        try(Reader reader = new FileReader(configFile)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> config = GSON.fromJson(reader, type);

            if(config != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> modulesConfig = (Map<String, Object>) config.get("modules");
                if(modulesConfig != null) {
                    for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> moduleConfig = (Map<String, Object>) modulesConfig.get(module.getName());
                        if(moduleConfig != null) {
                            Boolean enabled = (Boolean) moduleConfig.get("enabled");
                            if(enabled != null) {
                                module.setEnabled(enabled);
                            }

                            @SuppressWarnings("unchecked")
                            Map<String, Object> settings = (Map<String, Object>) moduleConfig.get("settings");
                            if(settings != null) {
                                for(Setting<?> setting : module.getSettings()) {
                                    Object settingData = settings.get(setting.getName());
                                    if(settingData != null) {
                                        if(settingData instanceof Map) {
                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> settingMap = (Map<String, Object>) settingData;
                                            Object value = settingMap.get("value");
                                            if(value != null) {
                                                setting.setValueFromObject(value);
                                            }
                                        } else {
                                            setting.setValueFromObject(settingData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                Map<String, Double> keybinds = (Map<String, Double>) config.get("keybinds");
                if(keybinds != null) {
                    KeybindManager keybindManager = KeybindManager.getInstance();
                    for(Map.Entry<String, Double> entry : keybinds.entrySet()) {
                        Module module = AlyaClient.INSTANCE.getModuleRepository().getModuleByName(entry.getKey());
                        if(module != null) {
                            keybindManager.bind(module, entry.getValue().intValue());
                        }
                    }
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lists all configuration files available in the configuration folder.
     * The method scans the folder specified by the constant CONFIG_FOLDER
     * and retrieves names of all files ending with the ".json" extension,
     * excluding the extension from the returned names.
     *
     * @return an array of configuration file names without extensions,
     * or an empty array if no configuration files are found.
     */
    public static String[] listConfigs() {
        File folder = new File(CONFIG_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if(files == null) return new String[0];

        String[] configNames = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            configNames[i] = files[i].getName().replace(".json", "");
        }
        return configNames;
    }

    /**
     * Creates a backup of the current config before loading a new one
     */
    public static void backupCurrentConfig() {
        String backupName = "backup_" + System.currentTimeMillis();
        saveConfig(backupName);
    }

    /**
     * Deletes a config file
     */
    public static boolean deleteConfig(String name) {
        File configFile = new File(CONFIG_FOLDER + name + ".json");
        return configFile.exists() && configFile.delete();
    }

    /**
     * Checks if a config exists
     */
    public static boolean configExists(String name) {
        File configFile = new File(CONFIG_FOLDER + name + ".json");
        return configFile.exists();
    }
}
