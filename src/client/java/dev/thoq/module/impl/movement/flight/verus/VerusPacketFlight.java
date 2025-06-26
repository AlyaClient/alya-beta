/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.RyeClient;
import dev.thoq.module.impl.visual.DebugModule;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class VerusPacketFlight {
    public void cancelPackets(Packet<?> packet, CallbackInfo callbackInfo) {
        if(packet instanceof PlayerInteractBlockC2SPacket) {
            callbackInfo.cancel();
        }

        if(packet instanceof PlayerMoveC2SPacket.LookAndOnGround) {
            callbackInfo.cancel();
        }
    }

    public void sendVerusPackets(MinecraftClient mc) {
        if(mc.player == null || mc.getNetworkHandler() == null) return;
        boolean debug = RyeClient.INSTANCE.getModuleRepository().getModule(DebugModule.class).isEnabled();

        if(debug) ChatUtility.sendDebug("Sending funny packets...");
        Vec3d playerPos = mc.player.getPos().add(0, -1, 0);
        BlockPos blockPos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);

        if(debug) ChatUtility.sendDebug("RESULT: " + hitResult);

        float yaw = mc.player.getYaw();
        boolean onGround = true;
        boolean horizontalCollision = mc.player.horizontalCollision;

        PlayerInteractBlockC2SPacket placePacket = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);

        PlayerMoveC2SPacket lookDownPacket = new PlayerMoveC2SPacket.LookAndOnGround(
                yaw,
                90.0f,
                onGround,
                horizontalCollision
        );

        mc.getNetworkHandler().sendPacket(lookDownPacket);
        mc.getNetworkHandler().sendPacket(placePacket);
    }

    public void verusFlight(MinecraftClient mc, GameOptions options) {
        if(mc.player == null) return;

        MovementUtility.setMotionY(0);

        if(options.forwardKey.isPressed()) {
            MovementUtility.setSpeed(0.33);
        } else {
            MovementUtility.setSpeed(0);
        }
    }
}
