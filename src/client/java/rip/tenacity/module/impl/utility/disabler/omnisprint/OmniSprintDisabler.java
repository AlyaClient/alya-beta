/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.module.impl.utility.disabler.omnisprint;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.PacketSendEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
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