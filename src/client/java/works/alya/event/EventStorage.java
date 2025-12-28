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

package works.alya.event;

public final class EventStorage<T> {

    private final Object owner;
    private final IEventListener<T> callback;

    public EventStorage(final Object owner, final IEventListener<T> callback) {
        this.owner = owner;
        this.callback = callback;
    }

    public Object getOwner() {
        return owner;
    }

    public IEventListener<T> getCallback() {
        return callback;
    }

}
