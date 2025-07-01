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

package dev.thoq.event.impl;


import dev.thoq.event.Event;
import net.minecraft.entity.Entity;

/**
 * Event fired when calculating reach distance for player interactions.
 * This allows modules to modify the reach distance.
 */
public final class ReachEvent extends Event {
    private double reachDistance;
    private Entity targetEntity;

    /**
     * Creates a new ReachEvent with the specified reach distance.
     *
     * @param reachDistance The default reach distance
     */
    public ReachEvent(final double reachDistance) {
        this.reachDistance = reachDistance;
    }

    /**
     * Creates a new ReachEvent with the specified reach distance and target entity.
     *
     * @param reachDistance The default reach distance
     * @param targetEntity The entity being reached for, can be null
     */
    public ReachEvent(final double reachDistance, final Entity targetEntity) {
        this.reachDistance = reachDistance;
        this.targetEntity = targetEntity;
    }

    /**
     * Gets the current reach distance.
     *
     * @return The reach distance
     */
    public double getReachDistance() {
        return reachDistance;
    }

    /**
     * Sets the reach distance.
     *
     * @param reachDistance The new reach distance
     */
    public void setReachDistance(final double reachDistance) {
        this.reachDistance = reachDistance;
    }

    /**
     * Gets the target entity, if any.
     *
     * @return The target entity, or null if none
     */
    public Entity getTargetEntity() {
        return targetEntity;
    }

    /**
     * Sets the target entity.
     *
     * @param targetEntity The new target entity
     */
    public void setTargetEntity(final Entity targetEntity) {
        this.targetEntity = targetEntity;
    }
}