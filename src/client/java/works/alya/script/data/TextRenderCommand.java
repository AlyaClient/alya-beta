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

package works.alya.script.data;

import net.minecraft.client.gui.DrawContext;
import works.alya.script.interfaces.IRenderCommand;
import works.alya.utilities.misc.ChatUtility;
import works.alya.utilities.render.TextRendererUtility;

/**
 * Command for rendering text.
 */
public record TextRenderCommand(String text, int x, int y, int color, boolean shadow) implements IRenderCommand {

    @Override
    public void execute(DrawContext context) {
        TextRendererUtility.renderText(context, text, color, x, y, shadow);
    }
}