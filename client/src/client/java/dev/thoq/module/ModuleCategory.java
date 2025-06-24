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

/**
 * Represents a category for different types of modules. Each category groups
 * modules based on their functionality, such as combat-related modules,
 * movement-related modules, and so on.
 * The available categories are:
 * - COMBAT: Modules related to combat functionalities.
 * - MOVEMENT: Modules focused on movement enhancements or modifications.
 * - PLAYER: Modules that enhance or modify player-specific behaviors.
 * - VISUAL: Modules related to visuals and rendering.
 * Each category is associated with a display name for user-friendly identification.
 */
public enum ModuleCategory {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    VISUAL("Visual");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
