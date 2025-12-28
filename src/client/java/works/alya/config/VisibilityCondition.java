/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
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