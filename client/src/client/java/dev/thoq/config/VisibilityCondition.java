package dev.thoq.config;

/**
 * Interface for defining visibility conditions for settings.
 * Used to show or hide settings based on the value of other settings.
 */
@FunctionalInterface
public interface VisibilityCondition {
    /**
     * Determines whether a setting should be visible.
     *
     * @return true if the setting should be visible, false otherwise
     */
    boolean isVisible();
}