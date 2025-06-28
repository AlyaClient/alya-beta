/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

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