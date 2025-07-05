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

package dev.thoq.module.impl.utility.disabler.spartan;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class SpartanDisabler extends SubModule {
    private static int timeRunning = 0;

    public SpartanDisabler(Module parent) {
        super("Spartan", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || mc.getNetworkHandler() == null) return;
        if(!event.isPre()) return;

        timeRunning++;

        PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(
                Hand.MAIN_HAND,
                0,
                mc.player.getYaw(),
                mc.player.getPitch()
        );

        if(timeRunning > 20) {
            mc.getNetworkHandler().sendPacket(playerInteractItemC2SPacket);
            timeRunning = 0;
        }
    };

    @Override
    public void reset() {
        timeRunning = 0;
    }
}
