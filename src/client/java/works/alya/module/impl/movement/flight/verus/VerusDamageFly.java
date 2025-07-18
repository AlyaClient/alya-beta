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

package works.alya.module.impl.movement.flight.verus;

import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;
import works.alya.utilities.misc.ChatUtility;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VerusDamageFly extends SubModule {
    static boolean messageSent = false;

    public VerusDamageFly(final Module parent) {
        super("VerusDamage", parent);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(this.mc.player == null || this.mc.options == null) return;
        if(!messageSent) {
            ChatUtility.sendMessage("Please take damage to begin fly!");
            messageSent = true;
        }

        boolean up = this.mc.options.jumpKey.isPressed();
        boolean damage = this.mc.player.hurtTime > 0;
        double xVelocity = this.mc.player.getVelocity().x;
        double zVelocity = this.mc.player.getVelocity().z;
        double x = this.mc.player.getX();
        double y = this.mc.player.getY();
        double z = this.mc.player.getZ();
        float yaw = this.mc.player.getYaw();
        float pitch = this.mc.player.getPitch();

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

        if(this.mc.player.fallDistance > 3.5) {
            PlayerMoveC2SPacket playerMoveC2SPacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false);
            this.mc.player.setVelocity(xVelocity, 0f, zVelocity);
            this.mc.player.networkHandler.sendPacket(playerMoveC2SPacket);
            this.mc.player.fallDistance = 0;
        }
    };

    public void reset() {
        messageSent = false;
    }
}
