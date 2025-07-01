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

package dev.thoq.event.pool;

import dev.thoq.event.IEventListener;

public final class EventSubscriber<Event> {

    private final Object subscriber;
    private final IEventListener<Event> eventListener;

    public EventSubscriber(final Object subscriber, final IEventListener<Event> eventListener) {
        this.subscriber = subscriber;
        this.eventListener = eventListener;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public IEventListener<Event> getEventListener() {
        return eventListener;
    }
}
