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

package works.alya.event.impl;


import works.alya.event.Event;
import net.minecraft.network.packet.Packet;

public final class PacketReceiveEvent extends Event {

    private Packet<?> packet;

    public PacketReceiveEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }


    public void setPacket(final Packet<?> packet) {
        this.packet = packet;
    }

}
