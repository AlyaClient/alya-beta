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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import works.alya.script.data.NumberSettingData;
import works.alya.utilities.misc.ChatUtility;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Lua script in the Alya Client.
 * Contains script metadata and execution environment.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class Script {
    private final File file;
    private final String name;
    private final String description;
    private final Globals globals;
    private final Map<String, Object> settings = new HashMap<>();
    private final Set<String> reportedErrors = new HashSet<>();

    /**
     * Creates a new Script instance.
     *
     * @param file        The Lua script file
     * @param name        The name of the script
     * @param description The description of the script
     * @param globals     The Lua globals environment
     */
    public Script(File file, String name, String description, Globals globals) {
        this.file = file;
        this.name = name;
        this.description = description;
        this.globals = globals;

        loadSettings();
    }

    /**
     * Loads settings defined in the script.
     */
    private void loadSettings() {
        LuaValue settingsTable = globals.get("settings");
        if(!settingsTable.isnil() && settingsTable.istable()) {
            LuaValue[] keys = settingsTable.checktable().keys();
            for(LuaValue key : keys) {
                String settingName = key.tojstring();
                LuaValue settingValue = settingsTable.get(key);

                if(settingValue.isboolean()) {
                    settings.put(settingName, settingValue.toboolean());
                } else if(settingValue.istable()) {
                    LuaValue valueField = settingValue.get("value");
                    LuaValue minField = settingValue.get("min");
                    LuaValue maxField = settingValue.get("max");

                    if(!valueField.isnil()) {
                        if(valueField.isint()) {
                            int value = valueField.toint();
                            int min = minField.isnil() ? Integer.MIN_VALUE : minField.toint();
                            int max = maxField.isnil() ? Integer.MAX_VALUE : maxField.toint();
                            settings.put(settingName, new NumberSettingData(value, min, max));
                        } else if(valueField.isnumber()) {
                            double value = valueField.todouble();
                            double min = minField.isnil() ? Double.MIN_VALUE : minField.todouble();
                            double max = maxField.isnil() ? Double.MAX_VALUE : maxField.todouble();
                            settings.put(settingName, new NumberSettingData(value, min, max));
                        } else if(valueField.isstring()) {
                            settings.put(settingName, valueField.tojstring());
                        }
                    }
                } else if(settingValue.isint()) {
                    settings.put(settingName, new NumberSettingData(settingValue.toint(), 0, 100));
                } else if(settingValue.isnumber()) {
                    settings.put(settingName, new NumberSettingData(settingValue.todouble(), 0.0, 100.0));
                } else if(settingValue.isstring()) {
                    settings.put(settingName, settingValue.tojstring());
                }
            }
        }
    }

    /**
     * Calls a function in the script.
     *
     * @param functionName The name of the function to call
     * @param args         The arguments to pass to the function
     */
    public void callFunction(String functionName, LuaValue... args) {
        LuaValue function = globals.get(functionName);
        if(function.isfunction()) {
            try {
                function.invoke(LuaValue.varargsOf(args));
            } catch(LuaError luaError) {
                String errorMessage = luaError.getMessage();
                String errorKey = name + ":" + functionName + ":" + errorMessage;

                if(!reportedErrors.contains(errorKey)) {
                    reportedErrors.add(errorKey);
                    String formattedError = "Error in script '" + name + "', function '" + functionName + "': " + errorMessage;
                    ChatUtility.sendError(formattedError);
                    luaError.printStackTrace();
                }
            } catch(Exception ex) {
                String errorKey = name + ":" + functionName + ":" + ex.getClass().getSimpleName();

                if(!reportedErrors.contains(errorKey)) {
                    reportedErrors.add(errorKey);
                    ChatUtility.sendError("Error calling function '" + functionName + "' in script '" + name + "'");
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Clears the reported errors set, allowing errors to be reported again.
     * This can be useful when reloading scripts or when you want to reset error reporting.
     */
    public void clearReportedErrors() {
        reportedErrors.clear();
    }

    /**
     * Gets the file for this script.
     *
     * @return The script file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the name of this script.
     *
     * @return The script name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this script.
     *
     * @return The script description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the Lua globals environment for this script.
     *
     * @return The Lua globals
     */
    public Globals getGlobals() {
        return globals;
    }

    /**
     * Gets the settings for this script.
     *
     * @return Map of setting name to value
     */
    public Map<String, Object> getSettings() {
        return settings;
    }
}