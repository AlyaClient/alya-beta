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

package works.alya.module.impl.utility.nofall.vanilla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaNoFall {
    public static void vanillaNoFall(MinecraftClient mc) {
        if(mc.player == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        PlayerMoveC2SPacket playerMoveC2SPacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false);

        mc.player.networkHandler.sendPacket(playerMoveC2SPacket);
        mc.player.fallDistance = 0;
    }
}
