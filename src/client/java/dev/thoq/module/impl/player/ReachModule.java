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

package dev.thoq.module.impl.player;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

@SuppressWarnings("SameParameterValue")
public class ReachModule extends Module {

    /**
     * @credit: NezhaAnticheat - for funny name
     */
    private static final BooleanSetting remoteSpleefHacks = new BooleanSetting("RemoteSpleefHacks", "Allows you to hit blocks from very far away", false);

    public ReachModule() {
        super("Reach", "billy big-arms", ModuleCategory.PLAYER);

        addSetting(remoteSpleefHacks);
    }

    private BlockHitResult raycast(double maxDistance) {
        if(mc.player == null || mc.world == null) return null;

        Vec3d start = mc.player.getCameraPosVec(1.0f);
        Vec3d direction = mc.player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(maxDistance));

        RaycastContext context = new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        );

        BlockHitResult result = mc.world.raycast(context);

        if(result.getType() == HitResult.Type.BLOCK) {
            return result;
        }

        return null;
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(remoteSpleefHacks.getValue() && mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
            if(mc.options.attackKey.isPressed()) {
                BlockHitResult hitResult = raycast(100.0);

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
                }
            }
        }
    };
}