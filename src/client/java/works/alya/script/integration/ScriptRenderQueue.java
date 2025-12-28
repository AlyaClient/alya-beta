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

package works.alya.script.integration;

import works.alya.AlyaClient;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.script.core.Script;
import works.alya.script.data.RectRenderCommand;
import works.alya.script.data.TextRenderCommand;
import works.alya.script.interfaces.IRenderCommand;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages rendering commands from scripts.
 * Stores rendering commands and executes them during render events.
 */
public final class ScriptRenderQueue {
    private static final List<IRenderCommand> renderCommands = new ArrayList<>();
    private static final Map<Script, List<IRenderCommand>> scriptCommandMap = new HashMap<>();
    private static Script currentScript = null;

    private static final IEventListener<Render2DEvent> render2DListener = event -> processRenderQueue(event.getContext());

    static {
        AlyaClient.getEventBus().subscribe(new Object() {
            @SuppressWarnings("unused")
            private final IEventListener<Render2DEvent> render2DEvent = render2DListener;
        });
    }

    /**
     * Sets the current script that is adding commands.
     *
     * @param script The current script
     */
    public static void setCurrentScript(final Script script) {
        currentScript = script;
    }

    /**
     * Clears all rendering commands for a specific script.
     *
     * @param script The script to clear commands for
     */
    public static void clearCommandsForScript(final Script script) {
        if (script == null) return;

        synchronized(renderCommands) {
            final List<IRenderCommand> commands = scriptCommandMap.get(script);
            if (commands != null) {
                renderCommands.removeAll(commands);
                commands.clear();
            }
        }
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
    public static void addTextRenderCommand(final String text, final int x, final int y, final int color, final boolean shadow) {
        final TextRenderCommand command = new TextRenderCommand(text, x, y, color, shadow);
        addCommandToScript(command);
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
    public static void addRectRenderCommand(final float x, final float y, final float width, final float height, final int color) {
        final RectRenderCommand command = new RectRenderCommand(x, y, width, height, color);
        addCommandToScript(command);
    }

    /**
     * Adds a command to the current script's command list.
     *
     * @param command The command to add
     */
    private static void addCommandToScript(final IRenderCommand command) {
        synchronized(renderCommands) {
            renderCommands.add(command);

            if (currentScript != null) {
                scriptCommandMap.computeIfAbsent(currentScript, k -> new ArrayList<>()).add(command);
            }
        }
    }

    /**
     * Processes all rendering commands in the queue.
     *
     * @param context The draw context
     */
    private static void processRenderQueue(final DrawContext context) {
        synchronized(renderCommands) {
            for(final IRenderCommand command : renderCommands) {
                command.execute(context);
            }
            renderCommands.clear();
            scriptCommandMap.values().forEach(List::clear);
        }
    }
}
