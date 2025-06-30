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

package dev.thoq.module.impl.combat;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.misc.RaycastUtility;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("SameParameterValue")
public class ReachModule extends Module {
    private static double lastReach = 0d;

    /**
     * @credit: NezhaAnticheat - for funny name
     */
    private static final BooleanSetting remoteSpleefHacks = new BooleanSetting("RemoteSpleefHacks", "Allows you to hit blocks from very far away", false);

    public ReachModule() {
        super("Reach", "billy big-arms", ModuleCategory.COMBAT);

        addSetting(remoteSpleefHacks);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(remoteSpleefHacks.getValue() && mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
            if(mc.options.attackKey.isPressed()) {
                BlockHitResult hitResult = RaycastUtility.raycast(mc, 10000.0);

                if(hitResult != null) {
                    BlockPos blockPos = hitResult.getBlockPos();
                    Direction face = hitResult.getSide();

                    PlayerActionC2SPacket startBreaking = new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                            blockPos,
                            face
                    );

                    PlayerActionC2SPacket stopBreaking = new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                            blockPos,
                            face
                    );

                    mc.getNetworkHandler().sendPacket(startBreaking);
                    mc.getNetworkHandler().sendPacket(stopBreaking);

                    lastReach = hitResult.getPos().distanceTo(mc.player.getEyePos());
                }
            }
        }
    };

    public static double getLastReach() {
        return lastReach;
    }
}