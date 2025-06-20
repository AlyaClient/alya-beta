package dev.thoq.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.thoq.RyeClient;
import dev.thoq.config.setting.Setting;
import dev.thoq.module.Module;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.thoq.RyeClient.LOGGER;

@SuppressWarnings("CallToPrintStackTrace")
public class ConfigManager {
    private static final String CONFIG_FOLDER = MinecraftClient.getInstance().runDirectory + "/" + RyeClient.getName() + "/configs/";
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

    public static void saveConfig(String name) {
        Map<String, Object> config = new LinkedHashMap<>();
        Map<String, Object> modulesConfig = new LinkedHashMap<>();
        Map<String, Integer> keybinds = new LinkedHashMap<>();

        for(Module module : RyeClient.INSTANCE.getModuleRepository().getModules()) {
            Map<String, Object> moduleConfig = new LinkedHashMap<>();

            moduleConfig.put("enabled", module.isEnabled());

            Map<String, Object> settings = new LinkedHashMap<>();
            for(Setting<?> setting : module.getSettings()) {
                settings.put(setting.getName(), setting.getValue());
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

                // Load modules config
                @SuppressWarnings("unchecked")
                Map<String, Object> modulesConfig = (Map<String, Object>) config.get("modules");
                if(modulesConfig != null) {
                    for(Module module : RyeClient.INSTANCE.getModuleRepository().getModules()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> moduleConfig = (Map<String, Object>) modulesConfig.get(module.getName());
                        if(moduleConfig != null) {
                            // Load enabled state
                            Boolean enabled = (Boolean) moduleConfig.get("enabled");
                            if(enabled != null) {
                                module.setEnabled(enabled);
                            }

                            // Load settings
                            @SuppressWarnings("unchecked")
                            Map<String, Object> settings = (Map<String, Object>) moduleConfig.get("settings");
                            if(settings != null) {
                                for(Setting<?> setting : module.getSettings()) {
                                    Object value = settings.get(setting.getName());
                                    if(value != null) {
                                        setting.setValueFromObject(value);
                                    }
                                }
                            }
                        }
                    }
                }

                // Load keybinds
                @SuppressWarnings("unchecked")
                Map<String, Double> keybinds = (Map<String, Double>) config.get("keybinds");
                if(keybinds != null) {
                    KeybindManager keybindManager = KeybindManager.getInstance();
                    for(Map.Entry<String, Double> entry : keybinds.entrySet()) {
                        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(entry.getKey());
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
}
