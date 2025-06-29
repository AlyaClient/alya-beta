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

import dev.thoq.event.impl.MotionEvent;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class VerusPacketFlight {

    public void sendVerusPackets(MinecraftClient mc, MotionEvent event) {
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        ChatUtility.sendDebug("Sending funny packets...");
        Vec3d playerPos = mc.player.getPos().add(0, -1, 0);
        BlockPos blockPos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);

        ChatUtility.sendDebug("RESULT: " + hitResult);

        PlayerInteractBlockC2SPacket placePacket = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);

        event.setPitch(90f);

        mc.getNetworkHandler().sendPacket(placePacket);
    }

    public void verusFlight(MinecraftClient mc, GameOptions options) {
        if(mc.player == null) return;

        MoveUtility.setMotionY(0);

        if(options.forwardKey.isPressed()) {
            MoveUtility.setSpeed(0.33);
        } else {
            MoveUtility.setSpeed(0);
        }
    }
}
