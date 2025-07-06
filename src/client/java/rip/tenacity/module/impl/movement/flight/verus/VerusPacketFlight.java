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

package rip.tenacity.module.impl.movement.flight.verus;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.misc.ChatUtility;
import rip.tenacity.utilities.player.MoveUtility;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class VerusPacketFlight extends SubModule {

    public VerusPacketFlight(final Module parent) {
        super("VerusPacket", parent);
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        ChatUtility.sendDebug("Sending funny packets...");
        Vec3d playerPos = mc.player.getPos().add(0, -1, 0);
        BlockPos blockPos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);

        ChatUtility.sendDebug("RESULT: " + hitResult);

        PlayerInteractBlockC2SPacket placePacket = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);

        event.setPitch(90f);

        mc.getNetworkHandler().sendPacket(placePacket);

        MoveUtility.setMotionY(0);

        if(this.mc.options.forwardKey.isPressed()) {
            MoveUtility.setSpeed(0.33);
        } else {
            MoveUtility.setSpeed(0);
        }
    };

}
