/*
 * Copyright (c) Rye Client 2025-2025.
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

import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusDamageFly {
    static boolean messageSent = false;

    public void damageFly(MinecraftClient mc, GameOptions options) {
        if(mc.player == null || options == null) return;
        if(!messageSent) {
            ChatUtility.sendMessage("Please take damage to begin fly!");
            messageSent = true;
        }

        boolean up = options.jumpKey.isPressed();
        boolean damage = mc.player.hurtTime > 0;
        double xVelocity = mc.player.getVelocity().x;
        double zVelocity = mc.player.getVelocity().z;
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        if(damage) {
            MoveUtility.setSpeed(8f, true);
        } else {
            MoveUtility.setSpeed(0f, true);
        }

        if(!up && damage) {
            MoveUtility.setMotionY(-0.02);
        }

        if(up && damage) {
            MoveUtility.setMotionY(1.5);
        }

        if(mc.player.fallDistance > 3.5) {
            PlayerMoveC2SPacket playerMoveC2SPacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false);
            mc.player.setVelocity(xVelocity, 0f, zVelocity);
            mc.player.networkHandler.sendPacket(playerMoveC2SPacket);
            mc.player.fallDistance = 0;
        }
    }

    public void reset() {
        messageSent = false;
    }
}
