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

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.RenderUtility;
import dev.thoq.utilities.render.TextRendererUtility;
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
                Identifier.of("rye", "images/rye_logo_white.png"),
                xPosition,
                yPosition,
                xPosition + padding * 6,
                yPosition + padding * 3,
                xPosition + padding * 6,
                yPosition + padding * 3,
                event.getContext()
        );

        int cordsY = mc.getWindow().getScaledHeight() - 10;
        Vec3d position = mc.player.getPos();
        String cordsText = String.format("XYZ: %.1f %.1f %.1f", position.x, position.y, position.z);

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