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

package works.alya.config;

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