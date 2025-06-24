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

package dev.thoq.module;

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
            repository.registerModule(module);
        }
    }
}
