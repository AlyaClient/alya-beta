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

package rip.tenacity.utilities.player;

import rip.tenacity.utilities.misc.DoubleBlockPos;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

public class PlayerUtility {

    public void applyDamage(MinecraftClient mc, int height) {
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        double x = mc.player.getX();
        double baseY = mc.player.getY();
        double z = mc.player.getZ();
        float pitch = mc.player.getPitch();
        float yaw = mc.player.getYaw();
        boolean onGround = false;
        boolean horizontalCollision = mc.player.horizontalCollision;

        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, baseY + height, z, yaw, pitch, onGround, horizontalCollision));
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, baseY, z, yaw, pitch, false, horizontalCollision));
    }

    /**
     * @return true if the player is over void, false otherwise
     * @author slqnt
     * @since 0.1
     */
    public boolean isOverVoid() {
        MinecraftClient mc = MinecraftClient.getInstance();

        if(mc.player == null) return false;
        if(mc.world == null) return false;

        if(!mc.player.isOnGround()) {
            for(double y = mc.player.getPos().y - 1; y >= 0.0; y--) {
                BlockPos blockPos = new DoubleBlockPos(mc.player.getPos().x, y, mc.player.getPos().z).toBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();

                if(!(block instanceof AirBlock))
                    return false;
            }

            return true;
        }

        return false;
    }
}
