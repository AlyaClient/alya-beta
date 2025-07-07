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

package works.alya.module.impl.visual;

import works.alya.AlyaClient;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.render.ColorUtility;
import works.alya.utilities.render.DragUtility;
import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.TextRendererUtility;
import works.alya.utilities.render.Theme;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("SameParameterValue")
public class SpeedMonitorModule extends Module {
    private final double[] bpsHistory = new double[200];
    private int historyIndex = 0;
    private int historySize = 0;
    private final DragUtility dragUtility = new DragUtility(4, 60);
    private static final int WHITE_COLOR = ColorUtility.getColor(ColorUtility.Colors.WHITE);
    private static final int MAX_HISTORY_SIZE = 200;
    private static final int MAX_RENDER_POINTS = 100;

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
        final Vector4f graphBgRadius = new Vector4f(6f, 6f, 6f, 6f);
        final int rectWidth = 170;
        final int rectHeight = 75;

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

        String currentBps = AlyaClient.getBps();
        double bpsValue = Double.parseDouble(currentBps);

        updateSpeedHistory(bpsValue);

        int contentX = xPosition + (padding - 1);
        int contentY = yPosition + (padding * 2) + 2;
        int graphY;
        int graphHeight = 50 - padding;
        int graphWidth = rectWidth - (padding * 2) + 2;

        Vec3d position = mc.player.getPos();
        String averageBps = AlyaClient.getBps();
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
                graphBgRadius,
                0x44000000
        );

        renderSpeedGraph(event, contentX, graphY, graphWidth, graphHeight);
    };

    private void updateSpeedHistory(double bpsValue) {
        bpsHistory[historyIndex] = bpsValue;
        historyIndex = (historyIndex + 1) % MAX_HISTORY_SIZE;
        if(historySize < MAX_HISTORY_SIZE) {
            historySize++;
        }
    }

    private void renderSpeedGraph(Render2DEvent event, int contentX, int graphY, int graphWidth, int graphHeight) {
        if(historySize <= 1) return;

        double maxBps = 100;

        int renderPoints = Math.min(historySize, MAX_RENDER_POINTS);
        int stepSize = Math.max(1, historySize / renderPoints);
        int graphWidthMinus1 = graphWidth - 1;
        int renderPointsMinus1 = renderPoints - 1;
        int startIndex = (historyIndex - historySize + MAX_HISTORY_SIZE) % MAX_HISTORY_SIZE;

        for(int i = 1; i < renderPoints; i++) {
            int prevDataIndex = (startIndex + (i - 1) * stepSize) % MAX_HISTORY_SIZE;
            int currDataIndex = (startIndex + i * stepSize) % MAX_HISTORY_SIZE;

            double prevBps = bpsHistory[prevDataIndex];
            double currBps = bpsHistory[currDataIndex];

            int x1 = contentX + ((i - 1) * graphWidthMinus1) / renderPointsMinus1;
            int y1 = graphY + graphHeight - (int) (Math.min(prevBps, maxBps) * graphHeight / maxBps);
            int x2 = contentX + (i * graphWidthMinus1) / renderPointsMinus1;
            int y2 = graphY + graphHeight - (int) (Math.min(currBps, maxBps) * graphHeight / maxBps);

            y1 = Math.max(y1, graphY);
            y2 = Math.max(y2, graphY);
            y1 = Math.min(y1, graphY + graphHeight);
            y2 = Math.min(y2, graphY + graphHeight);

            if(x1 != x2 || y1 != y2) {
                RenderUtility.drawLine(
                        event.getContext(),
                        x1, y1, x2, y2,
                        1.5f,
                        WHITE_COLOR
                );
            }
        }
    }

    private void handleMouseInput(int x, int y, int width, int height) {
        if(mc.mouse == null) return;

        double mouseX = mc.mouse.getX() / mc.getWindow().getScaleFactor();
        double mouseY = mc.mouse.getY() / mc.getWindow().getScaleFactor();

        boolean isMouseInside = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;

        if(GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            if(isMouseInside && !dragUtility.isDragging()) {
                dragUtility.startDragging((int) mouseX, (int) mouseY);
            }
            if(dragUtility.isDragging()) {
                dragUtility.updateDragPosition((int) mouseX, (int) mouseY);
            }
        } else {
            if(dragUtility.isDragging()) {
                dragUtility.stopDragging();
            }
        }
    }

    @Override
    protected void onDisable() {
        historyIndex = 0;
        historySize = 0;

        super.onDisable();
    }
}