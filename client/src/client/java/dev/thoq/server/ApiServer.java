package dev.thoq.server;

import dev.thoq.RyeClient;
import dev.thoq.config.ConfigManager;
import dev.thoq.config.KeybindManager;
import dev.thoq.config.Setting;
import dev.thoq.module.Module;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.plugin.bundled.CorsPluginConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "DuplicatedCode"})
public class ApiServer {
    private static final int PORT = 9595;
    private static ApiServer INSTANCE;
    private final Javalin app;

    private ApiServer() {
        app = Javalin.create(config -> config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost)));
        
        setupRoutes();
    }

    public static ApiServer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiServer();
        }
        return INSTANCE;
    }

    public void start() {
        app.start(PORT);
        RyeClient.LOGGER.info("API Server started on port " + PORT);
    }

    public void stop() {
        app.stop();
        RyeClient.LOGGER.info("API Server stopped");
    }

    private void setupRoutes() {
        app.get("/api/modules", this::getAllModules);

        app.get("/api/modules/{name}", this::getModule);
        
        app.post("/api/modules/{name}/toggle", this::toggleModule);
        
        app.post("/api/modules/{name}/enabled", this::setModuleEnabled);
        
        app.get("/api/modules/{name}/settings", this::getModuleSettings);
        
        app.post("/api/modules/{name}/settings/{settingName}", this::updateModuleSetting);
        
        app.get("/api/modules/{name}/keybind", this::getModuleKeybind);
        
        app.post("/api/modules/{name}/keybind", this::setModuleKeybind);
        
        app.get("/api/configs", this::getAllConfigs);
        
        app.post("/api/configs/{name}/save", this::saveConfig);
        
        app.post("/api/configs/{name}/load", this::loadConfig);
    }

    private void getAllModules(Context ctx) {
        List<Map<String, Object>> modules = RyeClient.INSTANCE.getModuleRepository().getModules().stream()
                .map(this::moduleToMap)
                .collect(Collectors.toList());
        
        ctx.json(modules);
    }

    private void getModule(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);

        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }

        ctx.json(moduleToMap(module));
    }

    private void toggleModule(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }
        
        module.toggle();
        ctx.json(Map.of("success", true, "enabled", module.isEnabled()));
    }

    private void setModuleEnabled(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }
        
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Boolean enabled = (Boolean) body.get("enabled");
            
            if (enabled == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(Map.of("error", "Missing 'enabled' field in request body"));
                return;
            }
            
            module.setEnabled(enabled);
            ctx.json(Map.of("success", true, "enabled", module.isEnabled()));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(Map.of("error", "Invalid request body: " + e.getMessage()));
        }
    }

    private void getModuleSettings(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }
        
        List<Map<String, Object>> settings = module.getSettings().stream()
                .map(this::settingToMap)
                .collect(Collectors.toList());
        
        ctx.json(settings);
    }

    private void updateModuleSetting(Context ctx) {
        String moduleName = ctx.pathParam("name");
        String settingName = ctx.pathParam("settingName");
        
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(moduleName);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + moduleName));
            return;
        }
        
        Setting<?> setting = module.getSetting(settingName);
        
        if (setting == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Setting not found: " + settingName));
            return;
        }
        
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Object value = body.get("value");
            
            if (value == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(Map.of("error", "Missing 'value' field in request body"));
                return;
            }
            
            setting.setValueFromObject(value);
            ctx.json(Map.of("success", true, "value", setting.getValue()));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(Map.of("error", "Invalid request body: " + e.getMessage()));
        }
    }

    private void getModuleKeybind(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }
        
        Integer key = KeybindManager.getInstance().getKeyForModule(module);
        ctx.json(Map.of("key", key != null ? key : -1));
    }

    @SuppressWarnings("ConstantValue")
    private void setModuleKeybind(Context ctx) {
        String name = ctx.pathParam("name");
        Module module = RyeClient.INSTANCE.getModuleRepository().getModuleByName(name);
        
        if (module == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(Map.of("error", "Module not found: " + name));
            return;
        }
        
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            Integer key = ((Number) body.get("key")).intValue();
            
            if (key == null) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.json(Map.of("error", "Missing 'key' field in request body"));
                return;
            }
            
            KeybindManager.getInstance().bind(module, key);
            ctx.json(Map.of("success", true, "key", key));
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(Map.of("error", "Invalid request body: " + e.getMessage()));
        }
    }

    private void getAllConfigs(Context ctx) {
        String[] configs = ConfigManager.listConfigs();
        ctx.json(configs);
    }

    private void saveConfig(Context ctx) {
        String name = ctx.pathParam("name");
        ConfigManager.saveConfig(name);
        ctx.json(Map.of("success", true));
    }

    private void loadConfig(Context ctx) {
        String name = ctx.pathParam("name");
        ConfigManager.loadConfig(name);
        ctx.json(Map.of("success", true));
    }

    private Map<String, Object> moduleToMap(Module module) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", module.getName());
        map.put("description", module.getDescription());
        map.put("enabled", module.isEnabled());
        
        Integer key = KeybindManager.getInstance().getKeyForModule(module);
        map.put("keybind", key != null ? key : -1);
        
        List<Map<String, Object>> settings = module.getSettings().stream()
                .map(this::settingToMap)
                .collect(Collectors.toList());
        map.put("settings", settings);
        
        return map;
    }

    private Map<String, Object> settingToMap(Setting<?> setting) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", setting.getName());
        map.put("description", setting.getDescription());
        map.put("value", setting.getValue());
        map.put("defaultValue", setting.getDefaultValue());
        map.put("minValue", setting.getMinValue());
        map.put("maxValue", setting.getMaxValue());
        map.put("type", setting.getValue().getClass().getSimpleName());
        
        return map;
    }
}