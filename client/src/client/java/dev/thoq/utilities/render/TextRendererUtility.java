package dev.thoq.utilities.render;

import dev.thoq.utilities.render.ColorUtility.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class TextRendererUtility {
    static MinecraftClient client = MinecraftClient.getInstance();

    public static void renderText(
            DrawContext context,
            String text,
            Colors color,
            int posX,
            int posY,
            boolean shadow
    ) {
        TextRenderer renderer = client.textRenderer;
        context.drawText(
                renderer,
                text,
                posX,
                posY,
                ColorUtility.getColor(color),
                shadow
        );
    }
}
