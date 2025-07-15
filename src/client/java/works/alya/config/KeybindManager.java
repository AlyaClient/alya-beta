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
import works.alya.module.Module;
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static works.alya.AlyaClient.LOGGER;

@SuppressWarnings("CallToPrintStackTrace")
public class KeybindManager {
    private static final KeybindManager INSTANCE = new KeybindManager();
    private final Map<Integer, Module> keybinds = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private boolean initialized = false;

    private KeybindManager() {
    }

    public static KeybindManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initialize the keybind manager and load saved keybindings
     * This should be called after all modules are registered
     */
    public void initialize() {
        if(!initialized) {
            ensureKeybindsFolder();
            loadKeybindings();
            initialized = true;
        }
    }

    private void ensureKeybindsFolder() {
        File folder = new File(getKeybindsFolder());
        if(!folder.exists()) {
            boolean result = folder.mkdirs();
            if(!result) {
                LOGGER.error("Failed to create keybindings folder!");
            }
        }
    }

    private String getKeybindsFolder() {
        return MinecraftClient.getInstance().runDirectory + "/" + AlyaClient.getName() + "/";
    }

    private String getKeybindsFile() {
        return getKeybindsFolder() + "keybindings.json";
    }

    public void bind(Module module, int keyCode) {
        keybinds.put(keyCode, module);
        if(initialized) {
            saveKeybindings();
        }
    }

    public void unbind(Module module) {
        keybinds.entrySet().removeIf(entry -> entry.getValue().equals(module));
        if(initialized) {
            saveKeybindings();
        }
    }

    public void handleKeyPress(int keyCode) {
        Module module = keybinds.get(keyCode);
        if(module != null) {
            module.toggle();
            if(Objects.equals(module.getName(), "ClickGUI")) return;
            ChatUtility.sendPrefixedMessage(
                    "Keybind",
                    module.getName() + " has been " + (module.isEnabled() ? "enabled" : "disabled"),
                    Formatting.LIGHT_PURPLE,
                    module.isEnabled() ? Formatting.GREEN : Formatting.RED
            );
        }
    }

    public Integer getKeyForModule(Module module) {
        for(Map.Entry<Integer, Module> entry : keybinds.entrySet()) {
            if(entry.getValue().equals(module)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean shouldHandleKeyPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.currentScreen == null;
    }

    /**
     * Saves the current keybindings to keybindings.json
     */
    private void saveKeybindings() {
        Map<String, Integer> keybindData = new HashMap<>();

        for(Map.Entry<Integer, Module> entry : keybinds.entrySet()) {
            keybindData.put(entry.getValue().getName(), entry.getKey());
        }

        File keybindsFile = new File(getKeybindsFile());
        try(Writer writer = new FileWriter(keybindsFile)) {
            String json = GSON.toJson(keybindData);
            writer.write(json);
        } catch(IOException ex) {
            LOGGER.error("Failed to save keybindings: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads keybindings from keybindings.json
     */
    private void loadKeybindings() {
        File keybindsFile = new File(getKeybindsFile());
        if(!keybindsFile.exists()) {
            return;
        }

        try(Reader reader = new FileReader(keybindsFile)) {
            Type type = new TypeToken<Map<String, Double>>() {}.getType();
            Map<String, Double> keybindData = GSON.fromJson(reader, type);

            if(keybindData != null) {
                keybinds.clear();

                for(Map.Entry<String, Double> entry : keybindData.entrySet()) {
                    Module module = AlyaClient.INSTANCE.getModuleRepository().getModuleByName(entry.getKey());
                    if(module != null) {
                        keybinds.put(entry.getValue().intValue(), module);
                    }
                }
            }
        } catch(IOException ex) {
            LOGGER.error("Failed to load keybindings: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Manually reload keybindings from file
     */
    public void reloadKeybindings() {
        if(initialized) {
            loadKeybindings();
        }
    }
}