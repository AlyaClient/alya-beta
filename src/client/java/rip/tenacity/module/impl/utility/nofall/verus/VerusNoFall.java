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

package rip.tenacity.module.impl.utility.nofall.verus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusNoFall {
    public static void verusNoFall(MinecraftClient mc) {
        if(mc.player == null) return;

        if(mc.player.fallDistance > 3.5) {
            double xVelocity = mc.player.getVelocity().x;
            double zVelocity = mc.player.getVelocity().z;
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();

            PlayerMoveC2SPacket playerMoveC2SPacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false);

            mc.player.setVelocity(xVelocity, 0f, zVelocity);
            mc.player.networkHandler.sendPacket(playerMoveC2SPacket);
            mc.player.fallDistance = 0;
        }
    }
}
