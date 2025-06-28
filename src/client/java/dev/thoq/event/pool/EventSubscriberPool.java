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

package dev.thoq.event.pool;

import dev.thoq.event.Event;
import dev.thoq.event.IEventListener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventSubscriberPool {

    private final Map<Type, List<EventSubscriber<?>>> subscriberCacheMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void subscribe(final Object subscriber) {
        for(final Field field : subscriber.getClass().getDeclaredFields()) {
            if(field.getType().isAssignableFrom(IEventListener.class)) {
                if(!field.isAccessible())
                    field.setAccessible(true);

                try {
                    final IEventListener<Event> listener = (IEventListener<Event>) field.get(subscriber);

                    if(listener != null) {
                        final Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        subscriberCacheMap.computeIfAbsent(type, list -> new CopyOnWriteArrayList<>()).add(new EventSubscriber<>(subscriber, listener));
                    }
                } catch(final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void unsubscribe(final Object subscriber) {
        subscriberCacheMap.values().forEach(eventSubscriberList -> eventSubscriberList.removeIf(eventSubscriber ->
                eventSubscriber.getSubscriber().equals(subscriber)));
    }

    @SuppressWarnings("unchecked")
    public void dispatch(final Event event) {
        final List<EventSubscriber<?>> eventSubscriberList = subscriberCacheMap.get(event.getClass());

        if(eventSubscriberList != null && !eventSubscriberList.isEmpty()) {
            eventSubscriberList.forEach(eventSubscriber -> {
                final EventSubscriber<Event> subscriber = (EventSubscriber<Event>) eventSubscriber;

                subscriber.getEventListener().invoke(event);
            });
        }
    }
}
