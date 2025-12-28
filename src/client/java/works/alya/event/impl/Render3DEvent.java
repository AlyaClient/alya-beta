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

@SuppressWarnings("unused")
public final class Render3DEvent extends Event {

    private float ticks;

    public Render3DEvent(final float ticks) {
        this.ticks = ticks;
    }

    public float getPartialTicks() {
        return ticks;
    }

    public void setTicks(final float ticks) {
        this.ticks = ticks;
    }
}