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

package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.DragUtility;
import dev.thoq.utilities.render.RenderUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.Theme;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class SpeedMonitorModule extends Module {
    private final List<Double> bpsHistory = new ArrayList<>();
    private final DragUtility dragUtility = new DragUtility(4, 37);

    public SpeedMonitorModule() {
        super("SpeedMonitor", "Speed Monitor", "Shows the average player speed and a graph", ModuleCategory.VISUAL);
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> render2DEvent = event -> {
        if(mc.player == null) return;

        final int padding = 3;
        final int xPosition = dragUtility.getX();
        final int yPosition = dragUtility.getY();
        final Vector4f radius = new Vector4f(10f, 10f, 10f, 10f);
        final int rectWidth = 200;
        final int rectHeight = 85;
        final int maxHistorySize = 600;

        handleMouseInput(xPosition, yPosition, rectWidth, rectHeight);

        RenderUtility.drawGradientRoundedRect(
                event.getContext(),
                xPosition,
                yPosition,
                rectWidth,
                rectHeight,
                radius,
                Theme.COLOR$1,
                Theme.COLOR$2
        );

        String currentBps = RyeClient.getBps();
        double bpsValue = Double.parseDouble(currentBps);

        bpsHistory.add(bpsValue);
        if(bpsHistory.size() > maxHistorySize) {
            bpsHistory.removeFirst();
        }

        int contentX = xPosition + (padding - 1);
        int contentY = yPosition + (padding * 2) + 2;
        int graphY;
        int graphHeight = 60 - padding;
        int graphWidth = rectWidth - (padding * 2) + 2;
        int cordsY = mc.getWindow().getScaledHeight() - 10;

        Vec3d position =  mc.player.getPos();
        String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);
        String averageBps = RyeClient.getBps();
        String averageText = String.format("Average: %.1f", Double.parseDouble(averageBps));

        TextRendererUtility.renderDynamicText(
                event.getContext(),
                "Speed",
                ColorUtility.Colors.WHITE,
                contentX + padding,
                contentY,
                false,
                "sf_pro_rounded_bold",
                10
        );

        int averageTextWidth = TextRendererUtility.getTextWidth(averageText);
        int averageTextX = contentX + graphWidth - averageTextWidth;

        TextRendererUtility.renderDynamicText(
                event.getContext(),
                averageText,
                ColorUtility.Colors.WHITE,
                averageTextX,
                contentY,
                false,
                "sf_pro_rounded_regular",
                10
        );

        contentY += TextRendererUtility.getTextHeight() + 4;
        graphY = contentY;

        RenderUtility.drawRoundedRect(
                event.getContext(),
                contentX,
                graphY,
                graphWidth,
                graphHeight + 5,
                radius,
                0x44000000
        );

        if(bpsHistory.size() > 1) {
            double maxBps = 100;

            for(int i = 1; i < bpsHistory.size(); i++) {
                double prevBps = bpsHistory.get(i - 1);
                double currBps = bpsHistory.get(i);

                int x1 = contentX + (int)((double)(i - 1) / (bpsHistory.size() - 1) * graphWidth);
                int y1 = graphY + graphHeight - (int)(Math.min(prevBps, maxBps) / maxBps * graphHeight);
                int x2 = contentX + (int)((double)i / (bpsHistory.size() - 1) * graphWidth);
                int y2 = graphY + graphHeight - (int)(Math.min(currBps, maxBps) / maxBps * graphHeight);

                y1 = Math.max(y1, graphY);
                y2 = Math.max(y2, graphY);

                RenderUtility.drawLine(
                        event.getContext(),
                        x1, y1, x2, y2,
                        2f,
                        ColorUtility.getColor(ColorUtility.Colors.WHITE)
                );
            }
        }
    };

    private void handleMouseInput(int x, int y, int width, int height) {
        if (mc.mouse == null) return;

        double mouseX = mc.mouse.getX() / mc.getWindow().getScaleFactor();
        double mouseY = mc.mouse.getY() / mc.getWindow().getScaleFactor();

        boolean isMouseInside = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            if (isMouseInside && !dragUtility.isDragging()) {
                dragUtility.startDragging((int)mouseX, (int)mouseY);
            }
            if (dragUtility.isDragging()) {
                dragUtility.updateDragPosition((int)mouseX, (int)mouseY);
            }
        } else {
            if (dragUtility.isDragging()) {
                dragUtility.stopDragging();
            }
        }
    }
}