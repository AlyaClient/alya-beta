/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.config;

import dev.thoq.module.Module;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KeybindManager {
    private static final KeybindManager INSTANCE = new KeybindManager();
    private final Map<Integer, Module> keybinds = new HashMap<>();

    private KeybindManager() {
    }

    public static KeybindManager getInstance() {
        return INSTANCE;
    }

    public void bind(Module module, int keyCode) {
        keybinds.put(keyCode, module);
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

    public void printBindings() {
        for(Map.Entry<Integer, Module> entry : keybinds.entrySet()) {
            ChatUtility.sendMessage(" - Key " + entry.getKey() + " -> " + entry.getValue().getName());
        }
    }

    public boolean shouldHandleKeyPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.currentScreen == null;
    }
}
