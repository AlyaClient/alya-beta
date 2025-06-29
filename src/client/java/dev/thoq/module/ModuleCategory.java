/*
 * Copyright (c) Rye Client 2025-2025.
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

/**
 * Enum representing the categorization of modules within a system.
 * Each category corresponds to a specific functional area or grouping of modules.
 * <p>
 * Categories include:
 * - COMBAT: Modules related to combat functionality.
 * - MOVEMENT: Modules that enhance or modify player movement.
 * - PLAYER: Modules that handle player-specific adjustments or behaviors.
 * - EXPLOIT: Modules that focus on exploiting the game or its mechanics.
 * - VISUAL: Modules that modify or enhance the visual elements of the game.
 */
public enum ModuleCategory {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    VISUAL("Visual");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
