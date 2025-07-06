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
import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.TextRendererUtility;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class HUDModule extends Module {

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);

        this.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if(mc.player == null) return;

        final int padding = 7;
        final int xPosition = 2;
        final int yPosition = 4;

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

        int lowerHudY = mc.getWindow().getScaledHeight() - 30;
        Vec3d position = mc.player.getPos();
        String fpsText = String.format("FPS: %s", AlyaClient.getFps());
        String bpsText = String.format("BPS: %s", AlyaClient.getBps());
        String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);

        TextRendererUtility.renderText(
                event.getContext(),
                fpsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                lowerHudY,
                false
        );

        lowerHudY += TextRendererUtility.getTextHeight() + 1;

        TextRendererUtility.renderText(
                event.getContext(),
                bpsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                lowerHudY,
                false
        );

        lowerHudY += TextRendererUtility.getTextHeight() + 1;

        TextRendererUtility.renderText(
                event.getContext(),
                cordsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                lowerHudY,
                false
        );
    };
}