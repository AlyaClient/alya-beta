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

package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.utilities.player.MovementUtility;
import dev.thoq.utilities.player.PlayerUtility;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusPacketLongjump {
    static boolean jumped = false;
    static boolean damaged = false;
    static boolean waitingForGround = false;

    public static void verusPacketLongjump(MinecraftClient mc) {
        if(mc.player == null) return;

        if(!damaged && !waitingForGround) {
            PlayerUtility.applyDamage(mc, 3);
            damaged = true;
            waitingForGround = true;
        }

        if(damaged && waitingForGround && mc.player.isOnGround()) {
            waitingForGround = false;

            float yaw = mc.player.getYaw();
            double motionX = -Math.sin(Math.toRadians(yaw)) * 0.3;
            double motionZ = Math.cos(Math.toRadians(yaw)) * 0.3;
            mc.player.setVelocity(motionX, mc.player.getVelocity().y, motionZ);

            double timerSpeed = 0.2f;

            TimerUtility.setTimerSpeed(timerSpeed);
            MovementUtility.setMotionY(0.42);

            MovementUtility.setSpeed(0.7f, true);

            new Thread(() -> {
                try {
                    Thread.sleep(Math.round(50 / timerSpeed));
                } catch(InterruptedException ignored) {
                }

                jumped = true;
            }).start();
        }

        if(jumped && mc.player.fallDistance > 2.5) {
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();

            PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, mc.player.horizontalCollision);
            mc.player.networkHandler.sendPacket(packet);
            mc.player.fallDistance = 0;
        }
    }

    public static void reset() {
        jumped = false;
        damaged = false;
        waitingForGround = false;
    }

    public static boolean hasJumped() {
        return jumped;
    }
}
