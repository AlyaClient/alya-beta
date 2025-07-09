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

package works.alya.script.integration;

import works.alya.config.setting.Setting;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.event.impl.Render2DEvent;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import works.alya.script.core.Script;
import works.alya.script.data.NumberSettingData;

import java.util.Map;

/**
 * Represents a Lua script as a module in the Alya Client.
 * Handles script execution and integration with the module system.
 */
@SuppressWarnings("unused")
public class ScriptModule extends Module {
    private Script script = null;

    /**
     * Creates a new ScriptModule instance.
     *
     * @param script The script to wrap as a module
     */
    public ScriptModule(Script script) {
        super(script.getName(), script.getDescription(), ModuleCategory.SCRIPTS);
        this.script = script;

        createSettings();
    }

    /**
     * Creates settings from script settings.
     */
    private void createSettings() {
        for(Map.Entry<String, Object> entry : script.getSettings().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if(value instanceof Boolean) {
                addSetting(new BooleanSetting(name, name, (Boolean) value));
            } else if(value instanceof NumberSettingData(Object value1, Object min, Object max)) {
                if(value1 instanceof Integer) {
                    addSetting(new NumberSetting<>(name, name,
                            (Integer) value1,
                            (Integer) min,
                            (Integer) max));
                } else if(value1 instanceof Double) {
                    addSetting(new NumberSetting<>(name, name,
                            (Double) value1,
                            (Double) min,
                            (Double) max));
                }
            } else if(value instanceof Integer) {
                addSetting(new NumberSetting<>(name, name, (Integer) value, 0, 100));
            } else if(value instanceof Double) {
                addSetting(new NumberSetting<>(name, name, (Double) value, 0.0, 100.0));
            } else if(value instanceof String) {
                ModeSetting modeSetting = new ModeSetting(name, name, (String) value);
                modeSetting.add((String) value);
                addSetting(modeSetting);
            }
        }
    }

    /**
     * Registers event listeners for the script.
     */
    private final IEventListener<Render2DEvent> render2DListener = event -> {
        if(isEnabled()) {
            script.callFunction("onRender2D", CoerceJavaToLua.coerce(event));
        }
    };

    private final IEventListener<MotionEvent> motionListener = event -> {
        if(isEnabled()) {
            script.callFunction("onMotionEvent", CoerceJavaToLua.coerce(event));
        }
    };

    private final IEventListener<TickEvent> tickListener = event -> {
        if(isEnabled()) {
            script.callFunction("onTickEvent", CoerceJavaToLua.coerce(event));
        }
    };

    @Override
    protected void onEnable() {
        super.onEnable();

        script.callFunction("onEnable");
    }

    @Override
    protected void onDisable() {
        script.callFunction("onDisable");

        // Clear any rendering commands from this script
        ScriptRenderQueue.clearCommandsForScript(script);

        super.onDisable();
    }

    /**
     * Gets the script associated with this module.
     *
     * @return The script
     */
    public Script getScript() {
        return script;
    }

    /**
     * Updates settings in the script based on module settings.
     */
    public void updateScriptSettings() {
        for(Setting<?> setting : getSettings()) {
            String name = setting.getName();
            Object value = setting.getValue();

            LuaValue settingsTable = script.getGlobals().get("settings");
            if(!settingsTable.isnil() && settingsTable.istable()) {
                settingsTable.set(name, CoerceJavaToLua.coerce(value));
            }
        }
    }
}
