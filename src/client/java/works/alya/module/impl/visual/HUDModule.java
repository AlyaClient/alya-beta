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

import org.jetbrains.annotations.NotNull;
import works.alya.AlyaClient;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.render.ColorUtility;
import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.TextRendererUtility;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import works.alya.utilities.render.Theme;

public class HUDModule extends Module {
    private final BooleanSetting showLogo = new BooleanSetting("Logo", "Show the Alya logo", false);
    private final BooleanSetting showFPS = new BooleanSetting("FPS", "Show the FPS", true);
    private final BooleanSetting showBPS = new BooleanSetting("BPS", "Show the BPS", true);
    private final BooleanSetting showTime = new BooleanSetting("Time", "Show the time", false);
    private final BooleanSetting showCords = new BooleanSetting("Cords", "Show the player's coordinates", true);

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);

        showFPS.setVisibilityCondition(() -> !showLogo.getValue());
        showBPS.setVisibilityCondition(() -> !showLogo.getValue());
        showTime.setVisibilityCondition(() -> !showLogo.getValue());

        addSetting(showLogo);
        addSetting(showFPS);
        addSetting(showBPS);
        addSetting(showTime);
        addSetting(showCords);

        this.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if(mc.player == null) return;

        final int padding = 7;
        final int xPosition = 2;
        final int yPosition = 4;

        if(showLogo.getValue()) {
            RenderUtility.drawImage(
                    Identifier.of("alya", "images/alya_logo.png"),
                    xPosition,
                    yPosition,
                    xPosition + padding * 6,
                    yPosition + padding * 6,
                    xPosition + padding * 6,
                    yPosition + padding * 6,
                    event.getContext()
            );
        } else {
            String hudText = getHudSuffix();
            String firstLetter = hudText.substring(0, 1);
            String rest = hudText.substring(1);

            float time = System.currentTimeMillis() / 1000.0f;
            float waveOffset = (float) 10 / Math.max(1, 3 - 1);
            float phase = time + waveOffset * 4.0f;
            float factor = (float) (Math.sin(phase) + 1.0) / 2.0f;

            int interpolatedColors = Theme.getInterpolatedColors(factor);
            TextRendererUtility.renderDynamicText(
                    event.getContext(),
                    firstLetter,
                    interpolatedColors,
                    xPosition,
                    yPosition + 1,
                    false,
                    "sf_pro_rounded_bold",
                    12
            );

            TextRendererUtility.renderDynamicText(
                    event.getContext(),
                    rest,
                    ColorUtility.getColor(ColorUtility.Colors.WHITE),
                    xPosition + TextRendererUtility.getTextWidth(firstLetter) + padding - 5,
                    yPosition + 1,
                    false,
                    "sf_pro_rounded_regular",
                    12
            );
        }

        if(showCords.getValue()) {
            final int lowerHudY = mc.getWindow().getScaledHeight() - 10;
            Vec3d position = mc.player.getPos();
            String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);

            TextRendererUtility.renderText(
                    event.getContext(),
                    cordsText,
                    ColorUtility.Colors.WHITE,
                    xPosition - 1,
                    lowerHudY,
                    false
            );
        }
    };

    private @NotNull String getHudSuffix() {
        String fpsText = String.format("§7[§r%s FPS§7]§r", AlyaClient.getFps());
        String bpsText = String.format("§7[§r%s BPS§7]§r", AlyaClient.getBps());
        String timeText = String.format("§7[§r%s§7]§r", AlyaClient.getTime());
        String hudText = AlyaClient.getName();

        if(showFPS.getValue())
            hudText += " " + fpsText;
        if(showBPS.getValue())
            hudText += " " + bpsText;
        if(showTime.getValue())
            hudText += " " + timeText;
        return hudText;
    }
}