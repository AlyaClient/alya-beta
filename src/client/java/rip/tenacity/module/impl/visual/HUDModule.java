/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.module.impl.visual;

import rip.tenacity.TenacityClient;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.Render2DEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.render.ColorUtility;
import rip.tenacity.utilities.render.RenderUtility;
import rip.tenacity.utilities.render.TextRendererUtility;
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
                Identifier.of("tenacity", "images/tenacity_logo.png"),
                xPosition,
                yPosition,
                xPosition + padding * 6,
                yPosition + padding * 6,
                xPosition + padding * 6,
                yPosition + padding * 6,
                event.getContext()
        );

        int lowerHudY = mc.getWindow().getScaledHeight() - 10;
        Vec3d position = mc.player.getPos();
        String fpsText = String.format("FPS: %s", TenacityClient.getFps());
        String bpsText = String.format("BPS: %s", TenacityClient.getBps());
        String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);

        TextRendererUtility.renderText(
                event.getContext(),
                fpsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                lowerHudY,
                false
        );

        lowerHudY += TextRendererUtility.getTextHeight() + 2;

        TextRendererUtility.renderText(
                event.getContext(),
                bpsText,
                ColorUtility.Colors.WHITE,
                xPosition - 1,
                lowerHudY,
                false
        );

        lowerHudY += TextRendererUtility.getTextHeight() + 2;

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