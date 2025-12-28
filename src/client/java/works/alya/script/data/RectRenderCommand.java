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

package works.alya.script.data;

import net.minecraft.client.gui.DrawContext;
import works.alya.script.interfaces.IRenderCommand;
import works.alya.utilities.render.RenderUtility;

/**
 * Command for rendering rectangles.
 */
public record RectRenderCommand(float x, float y, float width, float height, int color) implements IRenderCommand {

    @Override
    public void execute(final DrawContext context) {
        RenderUtility.drawRect(context, x, y, width, height, color);
    }
}