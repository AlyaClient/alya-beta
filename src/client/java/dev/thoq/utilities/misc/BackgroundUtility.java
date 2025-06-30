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

package dev.thoq.utilities.misc;

import dev.thoq.utilities.render.RenderUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class BackgroundUtility {
    public enum Version {
        TEXT,
        NO_TEXT
    }

    public static void drawBackground(DrawContext context, Version version) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int imageWidth = 1920;
        int imageHeight = 1080;

        float scale = Math.max(
                (float) screenWidth / imageWidth,
                (float) screenHeight / imageHeight
        ) * 1.05f;

        int renderWidth = (int) (imageWidth * scale);
        int renderHeight = (int) (imageHeight * scale);

        int posX = (screenWidth - renderWidth) / 2;
        int posY = (screenHeight - renderHeight) / 2;

        Identifier texture;

        if(Objects.requireNonNull(version) == Version.TEXT) {
            texture = Identifier.of("rye", "main-menu/rye_bg_text.png");
        } else {
            texture = Identifier.of("rye", "main-menu/rye_bg.png");
        }

        RenderUtility.drawImage(
                texture,
                posX,
                posY,
                renderWidth, renderHeight,
                imageWidth, imageHeight,
                context
        );
    }
}