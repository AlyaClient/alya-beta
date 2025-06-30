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

package dev.thoq.module.impl.movement.speed.ncp;

import dev.thoq.mixin.client.accessors.LivingEntityJumpAccessor;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class NCPSpeed {
    static int ticksRunning = 0;

    public void ncpSpeed(MinecraftClient mc, GameOptions options) {
        if(mc.player == null) return;

        boolean damaged = mc.player.hurtTime > 0;
        boolean isOnGround = mc.player.isOnGround();
        LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;

        livingEntityJumpAccessor.setJumpingCooldown(0);

        if(options.jumpKey.isPressed())
            return;

        if(!MoveUtility.isMoving())
            reset();

        if(MoveUtility.isMoving()) {
            ticksRunning++;

            ChatUtility.sendDebug(">ticksRunning<: " + ticksRunning);

            if(isOnGround) {
                MoveUtility.setSpeed(0.28335f, false);
            } else {
                MoveUtility.setSpeed(0.28f, false);
            }

            if(isOnGround) {
                mc.player.jump();
                reset();
            } else if(MoveUtility.isMoving() && ticksRunning == 6 && !damaged) {
                MoveUtility.setMotionY(-0.186567865);
                ChatUtility.sendDebug("PULLED");
            }
        }
    }

    public void reset() {
        ticksRunning = 0;
    }
}
