/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.ThemeUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class HUDModule extends Module {

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);
        this.setEnabled(true);
    }

    @Override
    protected void onRender(DrawContext context) {
        String displayText = getString();

        final int padding = 4;
        final int xPosition = 1;
        final int yPosition = 2;
        final int backgroundColor = ColorUtility.getColor(ColorUtility.Colors.PANEL);
        final int borderWidth = 2;
        final int textWidth = TextRendererUtility.getTextWidth(displayText);
        final int textHeight = mc.textRenderer.fontHeight;

        context.fill(
                xPosition - padding,
                yPosition - padding,
                xPosition + textWidth + padding,
                yPosition + textHeight + padding,
                backgroundColor
        );

        context.fill(
                xPosition - padding - borderWidth,
                yPosition - padding - borderWidth,
                xPosition + textWidth + padding + borderWidth,
                yPosition - padding,
                ColorUtility.getIntFromColor(ThemeUtility.getThemeColorFirst())
        );

        context.fill(
                xPosition - padding - borderWidth,
                yPosition - padding - borderWidth,
                xPosition - padding,
                yPosition + textHeight + padding + borderWidth,
                ColorUtility.getIntFromColor(ThemeUtility.getThemeColorFirst())
        );

        TextRendererUtility.renderText(
                context,
                displayText,
                ColorUtility.Colors.WHITE,
                xPosition,
                yPosition,
                true
        );
    }

    private static @NotNull String getString() {
        MinecraftClient mc = MinecraftClient.getInstance();
        String name = "§l" + "§d" + RyeClient.getName().charAt(0) + "§r§l" + RyeClient.getName().substring(1) + "§r";
        String time = new java.text.SimpleDateFormat("hh:mm a").format(new Date());

        if(mc.player != null) {
            String userName = mc.player.getName().getString();
            return String.format(
                    " %s | %s | %s",
                    name,
                    userName,
                    time
            );
        } else {
            return String.format(
                    " %s | %s",
                    name,
                    time
            );
        }
    }
}
