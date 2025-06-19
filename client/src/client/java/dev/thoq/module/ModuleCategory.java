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
    VISUAL("Visual"),
    CLIENT("Client");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
