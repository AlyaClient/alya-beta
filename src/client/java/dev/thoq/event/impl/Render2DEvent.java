/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.event.impl;

import dev.thoq.event.Event;
import net.minecraft.client.gui.DrawContext;

public final class Render2DEvent extends Event {

    private final DrawContext context;

    public Render2DEvent(final DrawContext drawContext) {
        this.context = drawContext;
    }

    public DrawContext getContext() {
        return context;
    }
}
