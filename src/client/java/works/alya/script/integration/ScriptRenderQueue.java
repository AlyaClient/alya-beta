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

package works.alya.script.integration;

import works.alya.AlyaClient;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.script.data.RectRenderCommand;
import works.alya.script.data.TextRenderCommand;
import works.alya.script.interfaces.IRenderCommand;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages rendering commands from scripts.
 * Stores rendering commands and executes them during render events.
 */
public class ScriptRenderQueue {
    private static final List<IRenderCommand> renderCommands = new ArrayList<>();

    static {
        AlyaClient.getEventBus().register((IEventListener<Render2DEvent>) event -> processRenderQueue(event.getContext()));
    }

    /**
     * Adds a text rendering command to the queue.
     *
     * @param text   The text to render
     * @param x      The x position
     * @param y      The y position
     * @param color  The text color
     * @param shadow Whether to render with shadow
     */
    public static void addTextRenderCommand(String text, int x, int y, int color, boolean shadow) {
        renderCommands.add(new TextRenderCommand(text, x, y, color, shadow));
    }

    /**
     * Adds a rectangle rendering command to the queue.
     *
     * @param x      The x position
     * @param y      The y position
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param color  The rectangle color
     */
    public static void addRectRenderCommand(float x, float y, float width, float height, int color) {
        renderCommands.add(new RectRenderCommand(x, y, width, height, color));
    }

    /**
     * Processes all rendering commands in the queue.
     *
     * @param context The draw context
     */
    private static void processRenderQueue(DrawContext context) {
        synchronized(renderCommands) {
            for(IRenderCommand command : renderCommands) {
                command.execute(context);
            }
            renderCommands.clear();
        }
    }
}