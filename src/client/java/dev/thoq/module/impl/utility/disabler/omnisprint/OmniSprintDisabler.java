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

package dev.thoq.module.impl.utility.disabler.omnisprint;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.PacketSendEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class OmniSprintDisabler extends SubModule {
    private static boolean serverSprintState = false;

    public OmniSprintDisabler(Module parent) {
        super("OmniSprint", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<PacketSendEvent> packetSendEvent = event -> {
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        if(event.getPacket() instanceof ClientCommandC2SPacket packet) {
            if(packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                if(serverSprintState) {
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    serverSprintState = false;
                }
                event.cancel();
            } else if(packet.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.cancel();
            }
        }
    };
}