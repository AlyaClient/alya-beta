/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.script.data;

import net.minecraft.client.gui.DrawContext;
import works.alya.script.interfaces.IRenderCommand;
import works.alya.utilities.render.TextRendererUtility;

/**
 * Command for rendering text.
 */
public record TextRenderCommand(String text, int x, int y, int color, boolean shadow) implements IRenderCommand {

    @Override
    public void execute(final DrawContext context) {
        TextRendererUtility.renderText(context, text, color, x, y, shadow);
    }
}