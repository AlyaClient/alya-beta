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

package works.alya.module.impl.movement.longjump.verus;

import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;
import works.alya.utilities.player.PlayerUtility;
import works.alya.utilities.player.TimerUtility;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusPacketLongjump extends SubModule {
    private final PlayerUtility playerUtility = new PlayerUtility();
    static boolean jumped = false;
    static boolean damaged = false;
    static boolean waitingForGround = false;

    public VerusPacketLongjump(final Module parent) {
        super("VerusPacket", parent);
    }

    private final IEventListener<TickEvent> onTick = event -> {
        if(mc.player == null) return;

        if(!damaged && !waitingForGround) {
            playerUtility.applyDamage(mc, 3);
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
            MoveUtility.setMotionY(0.42);

            MoveUtility.setSpeed(0.7f, true);

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
    };

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    public void reset() {
        jumped = false;
        damaged = false;
        waitingForGround = false;
    }

    public boolean hasJumped() {
        return jumped;
    }
}
