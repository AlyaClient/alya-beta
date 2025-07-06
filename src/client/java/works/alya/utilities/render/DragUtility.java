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

package works.alya.utilities.render;

import net.minecraft.client.MinecraftClient;
import works.alya.config.VisualManager;

/**
 * Utility class for handling dragging operations in GUI elements.
 */
public class DragUtility {
    private boolean dragging = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int elementX = 0;
    private int elementY = 0;

    /**
     * Creates a new DragUtility instance with the specified initial position.
     *
     * @param initialX The initial X position of the element
     * @param initialY The initial Y position of the element
     */
    public DragUtility(int initialX, int initialY) {
        this.elementX = initialX;
        this.elementY = initialY;
    }

    /**
     * Starts a dragging operation.
     *
     * @param mouseX The current mouse X position
     * @param mouseY The current mouse Y position
     */
    public void startDragging(int mouseX, int mouseY) {
        this.dragging = true;
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    /**
     * Updates the element position during a dragging operation.
     *
     * @param mouseX The current mouse X position
     * @param mouseY The current mouse Y position
     */
    public void updateDragPosition(int mouseX, int mouseY) {
        if(dragging) {
            int deltaX = mouseX - lastMouseX;
            int deltaY = mouseY - lastMouseY;

            elementX += deltaX;
            elementY += deltaY;

            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    /**
     * Stops the current dragging operation.
     */
    public void stopDragging() {
        if(this.dragging) {
            this.dragging = false;
            if(MinecraftClient.getInstance().player != null) {
                VisualManager.getInstance().saveVisualData();
            }
        }
    }

    /**
     * Checks if a dragging operation is in progress.
     *
     * @return True if dragging, false otherwise
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * Gets the current X position of the element.
     *
     * @return The X position
     */
    public int getX() {
        return elementX;
    }

    /**
     * Gets the current Y position of the element.
     *
     * @return The Y position
     */
    public int getY() {
        return elementY;
    }

    /**
     * Sets the X position of the element.
     *
     * @param x The new X position
     */
    public void setX(int x) {
        this.elementX = x;
    }

    /**
     * Sets the Y position of the element.
     *
     * @param y The new Y position
     */
    public void setY(int y) {
        this.elementY = y;
    }
}
