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

package works.alya.module.impl.utility.disabler.spartan;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public final class SpartanDisabler extends SubModule {
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
