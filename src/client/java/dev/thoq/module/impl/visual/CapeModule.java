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
import dev.thoq.utilities.render.RenderUtility;
import net.minecraft.util.Identifier;

// todo: idk make it 3d (lol)
public class CapeModule extends Module {
    public CapeModule() {
        super("Cape", "Draw a cape on player", ModuleCategory.VISUAL);
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if (mc.player == null || mc.world == null) return;
        renderCapeOnPlayer(event.getContext());
    };

    private void renderCapeOnPlayer(net.minecraft.client.gui.DrawContext context) {
        if (!isEnabled()) return;

        int screenX = context.getScaledWindowWidth() / 2;
        int screenY = context.getScaledWindowHeight() / 2;

        int capeWidth = 80;
        int capeHeight = 100;

        int capeX = screenX - capeWidth / 2;
        int capeY = screenY + 30;

        RenderUtility.drawImage(
                getCapeTexture(),
                capeX,
                capeY,
                capeWidth,
                capeHeight,
                64,
                32,
                context
        );
    }

    private Identifier getCapeTexture() {
        return Identifier.of("rye", "capes/cape-1.png");
    }
}