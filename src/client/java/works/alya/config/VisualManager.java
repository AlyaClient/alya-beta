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
import works.alya.module.ModuleCategory;
import works.alya.utilities.render.DragUtility;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static works.alya.AlyaClient.LOGGER;

@SuppressWarnings("CallToPrintStackTrace")
public class VisualManager {
    private static final VisualManager INSTANCE = new VisualManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private boolean initialized = false;
    private final Map<String, VisualModuleData> visualModulesData = new HashMap<>();

    private VisualManager() {
    }

    public static VisualManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initialize the visual manager and load saved visual module data
     * This should be called after all modules are registered
     */
    public void initialize() {
        if(!initialized) {
            ensureVisualsFolder();
            loadVisualData();
            initialized = true;
        }
    }

    private void ensureVisualsFolder() {
        File folder = new File(getVisualsFolder());
        if(!folder.exists()) {
            boolean result = folder.mkdirs();
            if(!result) {
                LOGGER.info("Failed to create visuals folder!");
            }
        }
    }

    private String getVisualsFolder() {
        return MinecraftClient.getInstance().runDirectory + "\\" + AlyaClient.getName() + "\\";
    }

    private String getVisualsFile() {
        return getVisualsFolder() + "visuals.json";
    }

    /**
     * Saves the current visual module data to visuals.json
     */
    public void saveVisualData() {
        for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
            if(module.getCategory() == ModuleCategory.VISUAL) {
                updateVisualModuleData(module);
            }
        }

        File visualsFile = new File(getVisualsFile());
        try(Writer writer = new FileWriter(visualsFile)) {
            String json = GSON.toJson(visualModulesData);
            writer.write(json);
        } catch(IOException ex) {
            LOGGER.error("Failed to save visual data: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Updates the visual module data for a specific module
     */
    public void updateVisualModuleData(Module module) {
        if(module.getCategory() != ModuleCategory.VISUAL) {
            return;
        }

        String moduleName = module.getName();
        VisualModuleData data = visualModulesData.getOrDefault(moduleName, new VisualModuleData());

        data.setEnabled(module.isEnabled());

        try {
            for(Field field : module.getClass().getDeclaredFields()) {
                if(field.getType() == DragUtility.class) {
                    field.setAccessible(true);
                    DragUtility dragUtility = (DragUtility) field.get(module);
                    if(dragUtility != null) {
                        data.setX(dragUtility.getX());
                        data.setY(dragUtility.getY());
                    }
                    break;
                }
            }
        } catch(Exception e) {
            LOGGER.error("Failed to get DragUtility from module {}: {}", moduleName, e.getMessage());
        }

        visualModulesData.put(moduleName, data);
    }

    /**
     * Loads visual module data from visuals.json
     */
    private void loadVisualData() {
        File visualsFile = new File(getVisualsFile());
        if(!visualsFile.exists()) {
            return;
        }

        try(Reader reader = new FileReader(visualsFile)) {
            Type type = new TypeToken<Map<String, VisualModuleData>>() {
            }.getType();
            Map<String, VisualModuleData> loadedData = GSON.fromJson(reader, type);

            if(loadedData != null) {
                visualModulesData.clear();
                visualModulesData.putAll(loadedData);
            }
        } catch(IOException ex) {
            LOGGER.error("Failed to load visual data: {}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Applies saved visual module data to modules
     * This should be called when joining a server/world
     */
    public void applyVisualData() {
        for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
            if(module.getCategory() == ModuleCategory.VISUAL) {
                applyVisualDataToModule(module);
            }
        }
    }

    /**
     * Applies saved visual data to a specific module
     */
    public void applyVisualDataToModule(Module module) {
        if(module.getCategory() != ModuleCategory.VISUAL) {
            return;
        }

        String moduleName = module.getName();
        VisualModuleData data = visualModulesData.get(moduleName);

        if(data != null) {
            module.setEnabled(data.isEnabled());

            try {
                for(Field field : module.getClass().getDeclaredFields()) {
                    if(field.getType() == DragUtility.class) {
                        field.setAccessible(true);
                        DragUtility dragUtility = (DragUtility) field.get(module);
                        if(dragUtility != null) {
                            dragUtility.setX(data.getX());
                            dragUtility.setY(data.getY());
                        }
                        break;
                    }
                }
            } catch(Exception e) {
                LOGGER.error("Failed to set DragUtility for module {}: {}", moduleName, e.getMessage());
            }
        }
    }

    /**
     * Data class for storing visual module information
     */
    private static class VisualModuleData {
        private boolean enabled = false;
        private int x = 4;
        private int y = 4;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}