/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
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
