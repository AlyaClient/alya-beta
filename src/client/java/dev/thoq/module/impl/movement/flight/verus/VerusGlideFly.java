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

package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;

public class VerusGlideFly {
    private static int timeRunning = 0;
    private static boolean messageSent = false;

    public void verusGlideFly(MinecraftClient mc, boolean clip) {
        if(mc.player == null) return;
        if(!messageSent) {
            ChatUtility.sendWarning("This fly does *NOT* new Verus, only old cracked versions!");
            messageSent = true;
        }

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();

        timeRunning++;

        if(clip && timeRunning >= 80) {
            mc.player.setPosition(posX, posY + 1, posZ);
            timeRunning = 0;
        }

        MoveUtility.setMotionY(-0.02);
        MoveUtility.setSpeed(0.1);
    }

    public void reset() {
        messageSent = false;
    }
}
