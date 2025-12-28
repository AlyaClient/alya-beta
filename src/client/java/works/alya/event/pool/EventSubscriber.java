/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.event.pool;

import works.alya.event.IEventListener;

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
