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

package works.alya.event.impl;

import works.alya.event.Event;
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
