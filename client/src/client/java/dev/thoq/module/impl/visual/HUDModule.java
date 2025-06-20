package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public class HUDModule extends Module {

    public HUDModule() {
        super("HUD", "Show world Heads Up Display", ModuleCategory.VISUAL);
    }

    @Override
    protected void onRender(DrawContext context) {
        String displayText = getString();

        final int padding = 2;
        final int xPosition = 1;
        final int yPosition = 2;
        final int backgroundColor = 0x90000000;
        int textWidth = TextRendererUtility.getTextWidth(displayText);
        int textHeight = mc.textRenderer.fontHeight;

        context.fill(
                xPosition - padding,
                yPosition - padding,
                xPosition + textWidth + padding,
                yPosition + textHeight + padding,
                backgroundColor
        );

        TextRendererUtility.renderText(
                context,
                displayText,
                ColorUtility.Colors.LAVENDER,
                xPosition,
                yPosition,
                true
        );
    }

    private static @NotNull String getString() {
        String name = RyeClient.getName();
        String edition = RyeClient.getEdition();
        String type = RyeClient.getType();
        String buildNumber = RyeClient.getBuildNumber();
        String fps = RyeClient.getFps();
        String bps = RyeClient.getBps();

        return String.format(
                "%s %s %s %s (FPS: %s, BPS: %s)",
                name,
                edition,
                type,
                buildNumber,
                fps,
                bps
        );
    }
}
