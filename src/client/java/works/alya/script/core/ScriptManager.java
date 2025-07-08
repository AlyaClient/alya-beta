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

package works.alya.script.core;

import net.minecraft.client.MinecraftClient;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import works.alya.AlyaClient;
import works.alya.script.api.LuaAPI;
import works.alya.script.integration.ScriptModule;
import works.alya.utilities.misc.ChatUtility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages Lua scripts for the Alya Client.
 * Handles loading, execution, and management of scripts.
 */
@SuppressWarnings({"CallToPrintStackTrace", "unused", "FieldCanBeLocal"})
public class ScriptManager {
    private static final ScriptManager INSTANCE = new ScriptManager();
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Script> scripts = new ArrayList<>();
    private final Map<String, ScriptModule> scriptModules = new HashMap<>();
    private final File scriptsDir;

    private ScriptManager() {
        scriptsDir = new File(mc.runDirectory, "Alya/scripts");
        if(!scriptsDir.exists()) {
            if(!scriptsDir.mkdirs()) {
                System.err.println("Failed to create scripts directory: " + scriptsDir.getAbsolutePath());
            }
        }
    }

    public static ScriptManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the script manager and loads all scripts from the scripts directory.
     */
    public void init() {
        loadScripts();
    }

    /**
     * Loads all Lua scripts from the scripts directory.
     */
    public void loadScripts() {
        scripts.clear();
        scriptModules.clear();

        if(!scriptsDir.exists()) {
            System.out.println("Scripts directory does not exist: " + scriptsDir.getAbsolutePath());
            return;
        }

        File[] scriptFiles = scriptsDir.listFiles((dir, name) -> name.endsWith(".lua"));
        if(scriptFiles == null) {
            return;
        }

        for(File scriptFile : scriptFiles) {
            try {
                Script script = loadScript(scriptFile);
                scripts.add(script);

                ScriptModule module = new ScriptModule(script);
                scriptModules.put(script.getName(), module);

                AlyaClient.INSTANCE.getModuleRepository().registerModule(module);
            } catch(Exception ex) {
                ChatUtility.sendError("Failed to load script: " + scriptFile.getName());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Loads a single Lua script from a file.
     *
     * @param file The Lua script file
     * @return The loaded Script object
     * @throws IOException If the script cannot be read
     */
    private Script loadScript(File file) throws IOException {
        try(FileReader reader = new FileReader(file)) {
            Globals globals = JsePlatform.standardGlobals();

            // Create a temporary script with default values
            Script tempScript = new Script(file, file.getName().replace(".lua", ""), "", globals);

            // Register the API with the script
            LuaAPI.register(globals, tempScript);

            // Load and execute the script
            LuaValue chunk = globals.load(reader, file.getName());
            chunk.call();

            // Get the actual name and description
            String name = globals.get("name").optjstring(file.getName().replace(".lua", ""));
            String description = globals.get("description").optjstring("No description provided");

            // Create the final script with the correct values
            Script finalScript = new Script(file, name, description, globals);

            // Update the current script in the API
            LuaAPI.updateCurrentScript(finalScript);

            return finalScript;
        }
    }

    /**
     * Gets all loaded scripts.
     *
     * @return List of loaded scripts
     */
    public List<Script> getScripts() {
        return scripts;
    }

    /**
     * Gets all script modules.
     *
     * @return Map of script name to ScriptModule
     */
    public Map<String, ScriptModule> getScriptModules() {
        return scriptModules;
    }

    /**
     * Gets the scripts directory.
     *
     * @return The scripts directory
     */
    public File getScriptsDir() {
        return scriptsDir;
    }
}
