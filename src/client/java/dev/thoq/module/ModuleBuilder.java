/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module;

import dev.thoq.config.KeybindManager;
import org.lwjgl.glfw.GLFW;

public class ModuleBuilder {
    private final ModuleRepository repository;

    private ModuleBuilder() {
        this.repository = ModuleRepository.getInstance();
    }

    public static ModuleBuilder create() {
        return new ModuleBuilder();
    }

    public void putAll(Module... modules) {
        for(Module module : modules) {
            if(module.getName().equals("ClickGUI"))
                KeybindManager.getInstance().bind(module, GLFW.GLFW_KEY_RIGHT_SHIFT);
            repository.registerModule(module);
        }
    }
}
