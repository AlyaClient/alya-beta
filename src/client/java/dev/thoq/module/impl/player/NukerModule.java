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
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.misc.RaycastUtility;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class NukerModule extends Module {
    private final ModeSetting modeSetting = new ModeSetting("Mode", "modes go brrrrr", "Instant", "Instant", "American", "RemoteBomb");
    private final BooleanSetting teleportToPlayerOverride = new BooleanSetting("PlayerPort", "Teleport to players instead of blocks", false);
    private final BooleanSetting clearStatsOnToggle = new BooleanSetting("ClearStats", "Clear stats on toggle", false);
    private int teleportCooldown = 0;
    private static int blocksDestroyed = 0;

    public NukerModule() {
        super("Nuker", "Destroy blocks automatically", ModuleCategory.PLAYER);

        addSetting(modeSetting);
        addSetting(teleportToPlayerOverride.setVisibilityCondition(() -> "American".equals(modeSetting.getValue())));
        addSetting(clearStatsOnToggle);
    }

    // TODO: Make it more then just boom world gone
    private final IEventListener<TickEvent> tickEvent = event -> {
        String mode = ((ModeSetting) getSetting("Mode")).getValue();
        switch(mode) {
            case "Instant": {
                if(mc.player == null || mc.world == null || mc.getNetworkHandler() == null || !event.isPre()) return;

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

                            blocksDestroyed++;
                        }
                    }
                }

                break;
            }

            case "American": {
                final int teleportDelay = 5; // ticks

                if(mc.player == null || mc.player.getGameMode() == null || mc.world == null || mc.getNetworkHandler() == null || !event.isPre())
                    return;

                World world = mc.world;
                BlockPos playerPos = mc.player.getBlockPos();
                int radius = 1;

                // eat all blocks around the player
                boolean foundBlocks = false;
                for(int x = -radius; x <= radius; x++) {
                    for(int y = -radius; y <= radius; y++) {
                        for(int z = -radius; z <= radius; z++) {
                            BlockPos blockPos = playerPos.add(x, y, z);
                            Block block = world.getBlockState(blockPos).getBlock();

                            if(block == Blocks.AIR) continue;
                            if(!mc.player.getGameMode().isCreative() && block == Blocks.BEDROCK) continue;

                            foundBlocks = true;

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
                            blocksDestroyed++;
                        }
                    }
                }

                if(!foundBlocks && teleportCooldown <= 0) {
                    BlockPos bestLocation = findLocationWithMostBlocks(world, playerPos);
                    if(bestLocation != null) {
                        ChatUtility.sendDebug("teleported");
                        mc.player.setPosition(bestLocation.getX() + 0.5, bestLocation.getY() + 1, bestLocation.getZ() + 0.5);
                        teleportCooldown = teleportDelay;
                    }
                }

                if(teleportCooldown > 0) {
                    teleportCooldown--;
                }

                break;
            }

            case "RemoteBomb": {
                if(mc.player == null || mc.player.getGameMode() == null || mc.world == null || mc.getNetworkHandler() == null || !event.isPre()) return;

                if(mc.options.attackKey.isPressed()) {
                    BlockHitResult hitResult = RaycastUtility.raycast(mc, 1000.0);

                    if(hitResult != null) {
                        BlockPos centerPos = hitResult.getBlockPos();
                        Direction face = hitResult.getSide();

                        int radius = 2;

                        for(int x = -radius; x <= radius; x++) {
                            for(int y = -radius; y <= radius; y++) {
                                for(int z = -radius; z <= radius; z++) {
                                    BlockPos blockPos = centerPos.add(x, y, z);
                                    Block block = mc.world.getBlockState(blockPos).getBlock();

                                    if(block == Blocks.AIR) continue;
                                    if(!mc.player.getGameMode().isCreative() && block == Blocks.BEDROCK) continue;

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

                                    blocksDestroyed++;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    };

    private BlockPos findLocationWithMostBlocks(World world, BlockPos currentPos) {
        if(teleportToPlayerOverride.getValue()) {
            return findNearestPlayer(world, currentPos);
        }
        
        BlockPos bestLocation = null;
        int maxBlocks = 0;
        int searchRadius = 50;
        int checkRadius = 3;

        // check every 10 blocks for optimization, it crashed a LOT
        for(int x = -searchRadius; x <= searchRadius; x += 10) {
            for(int y = 0; y <= 20; y += 5) { // y - only above ground
                for(int z = -searchRadius; z <= searchRadius; z += 10) {
                    BlockPos checkPos = currentPos.add(x, y, z);

                    if(checkPos.getSquaredDistance(currentPos) < 100) continue;

                    // check if teleport location is safe
                    if(!isSafeTeleportLocation(world, checkPos)) continue;

                    int blockCount = countBlocksInArea(world, checkPos, checkRadius);

                    if(blockCount > maxBlocks) {
                        maxBlocks = blockCount;
                        bestLocation = checkPos;
                    }
                }
            }
        }

        return maxBlocks > 10 ? bestLocation : null;
    }

    private BlockPos findNearestPlayer(World world, BlockPos currentPos) {
        if(mc.player == null || world == null) return null;
        
        double nearestDistance = Double.MAX_VALUE;
        BlockPos nearestPlayerPos = null;
        
        for(net.minecraft.entity.player.PlayerEntity player : world.getPlayers()) {
            // skip self
            if(player == mc.player) continue;
            
            BlockPos playerPos = player.getBlockPos();
            double distance = currentPos.getSquaredDistance(playerPos);
            
            if(distance < nearestDistance && distance > 100) { // 100 = 10^2
                // safe to teleport?
                if(isSafeTeleportLocation(world, playerPos)) {
                    nearestDistance = distance;
                    nearestPlayerPos = playerPos;
                }
            }
        }
        
        return nearestPlayerPos;
    }

    private boolean isSafeTeleportLocation(World world, BlockPos pos) {
        BlockPos headPos = pos.up();
        
        Block feetBlock = world.getBlockState(pos).getBlock();
        Block headBlock = world.getBlockState(headPos).getBlock();
        
        return (feetBlock == Blocks.AIR || isPassableBlock(feetBlock)) &&
           (headBlock == Blocks.AIR || isPassableBlock(headBlock));
    }

    private boolean isPassableBlock(Block block) {
        return block == Blocks.AIR ||
           block == Blocks.SHORT_DRY_GRASS ||
           block == Blocks.SHORT_GRASS ||
           block == Blocks.TALL_GRASS ||
           block == Blocks.WATER ||
           block == Blocks.SNOW ||
           block == Blocks.VINE ||
           block == Blocks.WHEAT ||
           block == Blocks.CARROTS ||
           block == Blocks.POTATOES;
    }

    private int countBlocksInArea(World world, BlockPos center, int radius) {
        int count = 0;

        for(int x = -radius; x <= radius; x++) {
            for(int y = 0; y <= 20; y += 5) { // only above ground
                for(int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = center.add(x, y, z);
                    Block block = world.getBlockState(blockPos).getBlock();

                    if(block != Blocks.AIR && block != Blocks.BEDROCK) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    @Override
    protected void onEnable() {
        if(clearStatsOnToggle.getValue())
            blocksDestroyed = 0;
    }

    @Override
    protected void onDisable() {
        if(clearStatsOnToggle.getValue())
            blocksDestroyed = 0;
    }

    public static int getBlocksDestroyed() {
        return blocksDestroyed;
    }
}