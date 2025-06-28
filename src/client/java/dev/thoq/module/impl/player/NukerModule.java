/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.player;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NukerModule extends Module {
    private final BooleanSetting americanMode = new BooleanSetting("American", "Eat everything", true);

    public NukerModule() {
        super("Nuker", "Destroy blocks automatically", ModuleCategory.PLAYER);

        addSetting(americanMode);
    }

    // TODO: Make it more then just boom world gone
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(americanMode.getValue() && mc.getNetworkHandler() != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if(mc.player == null || mc.world == null) return;

            World world = mc.world;
            BlockPos playerPos = mc.player.getBlockPos();

            int radius = 2;

            for(int x = -radius; x <= radius; x++) {
                for(int y = -radius; y <= radius; y++) {
                    for(int z = -radius; z <= radius; z++) {
                        BlockPos blockPos = playerPos.add(x, y, z);
                        Block block = world.getBlockState(blockPos).getBlock();

                        if(block == Blocks.AIR) continue;

                        PlayerActionC2SPacket startBreaking = new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                                blockPos,
                                Direction.UP
                                );

                        PlayerActionC2SPacket stopBreaking = new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                                blockPos,
                                Direction.UP
                        );

                        mc.getNetworkHandler().sendPacket(startBreaking);
                        mc.getNetworkHandler().sendPacket(stopBreaking);
                    }
                }
            }
        }
    };
}