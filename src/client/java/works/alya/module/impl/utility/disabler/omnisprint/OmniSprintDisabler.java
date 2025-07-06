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

package works.alya.module.impl.utility.disabler.omnisprint;

import works.alya.event.IEventListener;
import works.alya.event.impl.PacketSendEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
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