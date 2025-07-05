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
import dev.thoq.utilities.render.RenderUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.Theme;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class HUDModule extends Module {
    private final List<Double> bpsHistory = new ArrayList<>();

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);

        this.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if(mc.player == null) return;

        final int padding = 7;
        final int xPosition = 4;
        final int yPosition = 2;
        final Vector4f radius = new Vector4f(10f, 10f, 10f, 10f);
        final int rectWidth = 200;
        final int rectHeight = 120;
        final int maxHistorySize = 200;

        RenderUtility.drawImage(
                Identifier.of("rye", "images/rye_logo_white.png"),
                xPosition,
                yPosition,
                xPosition + padding * 6,
                yPosition + padding * 3,
                xPosition + padding * 6,
                yPosition + padding * 3,
                event.getContext()
        );

        RenderUtility.drawGradientRoundedRect(
                event.getContext(),
                xPosition,
                yPosition + (padding * 5),
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

        int contentX = xPosition + padding;
        int contentY = yPosition + (padding * 5) + 10;
        int graphY;
        int graphHeight = 30;
        int graphWidth = rectWidth - (padding * 6);
        int cordsY = mc.getWindow().getScaledHeight() - 10;
        Vec3d position =  mc.player.getPos();
        String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);
        contentY += 3;

        TextRendererUtility.renderMdText(
                event.getContext(),
                "Statistics",
                ColorUtility.Colors.WHITE,
                contentX,
                contentY,
                false
        );

        contentY += TextRendererUtility.getMdTextHeight() + (padding * 2);

        TextRendererUtility.renderText(
                event.getContext(),
                "FPS: " + RyeClient.getFps(),
                ColorUtility.Colors.WHITE,
                contentX,
                contentY,
                false
        );

        contentY += TextRendererUtility.getTextHeight() + 2;

        TextRendererUtility.renderText(
                event.getContext(),
                "BPS: " + currentBps,
                ColorUtility.Colors.WHITE,
                contentX,
                contentY,
                false
        );

        contentY += 20;

        TextRendererUtility.renderText(
                event.getContext(),
                "Speed Monitor",
                ColorUtility.Colors.WHITE,
                contentX,
                contentY,
                false
        );

        contentY += TextRendererUtility.getTextHeight() + 2;
        graphY = contentY;

        RenderUtility.drawRect(
                event.getContext(),
                contentX,
                graphY,
                graphWidth,
                graphHeight,
                0x44FFFFFF
        );

        if(bpsHistory.size() > 1) {
            double maxBps = bpsHistory.stream().mapToDouble(d -> d).max().orElse(10.0);
            maxBps = Math.max(maxBps, 10.0);

            for(int i = 1; i < bpsHistory.size(); i++) {
                double prevBps = bpsHistory.get(i - 1);
                double currBps = bpsHistory.get(i);

                int x1 = contentX + (int)((double)(i - 1) / (bpsHistory.size() - 1) * graphWidth);
                int y1 = graphY + graphHeight - (int)(prevBps / maxBps * graphHeight);
                int x2 = contentX + (int)((double)i / (bpsHistory.size() - 1) * graphWidth);
                int y2 = graphY + graphHeight - (int)(currBps / maxBps * graphHeight);

                RenderUtility.drawLine(
                        event.getContext(),
                        x1, y1, x2, y2,
                        2f,
                        ColorUtility.getColor(ColorUtility.Colors.WHITE)
                );
            }
        }

        String maxLabel = String.format("%.1f", bpsHistory.stream().mapToDouble(d -> d).max().orElse(10.0));
        TextRendererUtility.renderText(
                event.getContext(),
                maxLabel,
                ColorUtility.Colors.LIGHT_GRAY,
                contentX + graphWidth + 5,
                graphY,
                false
        );

        TextRendererUtility.renderText(
                event.getContext(),
                "0.0",
                ColorUtility.Colors.LIGHT_GRAY,
                contentX + graphWidth + 5,
                graphY + graphHeight - TextRendererUtility.getTextHeight(),
                false
        );

        TextRendererUtility.renderText(
                event.getContext(),
                cordsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                cordsY,
                false
        );
    };
}