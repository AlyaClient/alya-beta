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

package dev.thoq.module.impl.movement.scaffold;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("FieldCanBeLocal")
public class ScaffoldModule extends Module {
    
    private final BooleanSetting sprint = new BooleanSetting("Sprint", "Allow sprinting while scaffolding", true);
    private final BooleanSetting swing = new BooleanSetting("Swing", "Swing arm when placing blocks", true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate towards block placement", true);
    private final BooleanSetting tower = new BooleanSetting("Tower", "Enable tower mode when jumping", true);
    
    private final NumberSetting<Integer> searchRange = new NumberSetting<>("Search Range", "Block search range", 3, 1, 6);
    private final NumberSetting<Double> towerSpeed = new NumberSetting<>("Tower Speed", "Speed for tower mode", 0.42, 0.1, 1.0);
    private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Placement delay in milliseconds", 100, 0, 500);
    
    private final ModeSetting swingMode = new ModeSetting("Swing Mode", "How to swing arm", "Client", "Client", "Server");
    private final ModeSetting towerMode = new ModeSetting("Tower Mode", "Tower movement method", "Vanilla", "Vanilla", "Jump", "Motion");
    
    private int originalSlot = -1;
    private int blockSlot = -1;
    private long lastPlaceTime = 0;
    private float targetYaw, targetPitch;
    
    private BlockPlaceInfo placeInfo;
    
    public ScaffoldModule() {
        super("Scaffold", "Automatically places blocks beneath you", ModuleCategory.MOVEMENT);
        
        addSetting(sprint);
        addSetting(swing);
        addSetting(rotate);
        addSetting(tower);
        addSetting(searchRange);
        addSetting(delay);
        addSetting(swingMode.setVisibilityCondition(swing::getValue));
        addSetting(towerMode.setVisibilityCondition(tower::getValue));
        addSetting(towerSpeed.setVisibilityCondition(() -> tower.getValue() && towerMode.getValue().equals("Motion")));
    }
    
    @Override
    protected void onEnable() {
        if (mc.player != null) {
            originalSlot = mc.player.getInventory().getSelectedSlot();
        }
    }
    
    @Override
    protected void onDisable() {
        if (mc.player != null && originalSlot != -1) {
            mc.player.getInventory().setSelectedSlot(originalSlot);
        }
    }
    
    @Override
    protected void onMotion() {
        if (mc.player == null || mc.world == null) return;
        
        blockSlot = findBlockSlot();
        if (blockSlot == -1) return;

        mc.player.getInventory().setSelectedSlot(blockSlot);
        
        if (sprint.getValue() && MoveUtility.isMoving()) {
            MoveUtility.setSpeed(0.215);
        }
        
        placeInfo = findPlacePosition();
        if (placeInfo == null) return;
        
        if (rotate.getValue()) {
            calculateRotations();
            mc.player.setYaw(targetYaw);
            mc.player.setPitch(targetPitch);
        }
        
        if (tower.getValue() && mc.options.jumpKey.isPressed()) {
            handleTower();
        }
        
        if (System.currentTimeMillis() - lastPlaceTime >= delay.getValue()) {
            placeBlock();
        }
    }
    
    private int findBlockSlot() {
        if(mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                // TODO: Remove deprecated `.isLiquid()` call
                //noinspection deprecation
                if (blockItem.getBlock() != Blocks.AIR &&
                    !blockItem.getBlock().getDefaultState().isLiquid()) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private BlockPlaceInfo findPlacePosition() {
        if(mc.player == null || mc.world == null) return null;

        BlockPos playerPos = BlockPos.ofFloored(mc.player.getPos());
        BlockPos belowPos = playerPos.down();
        
        if (!mc.world.getBlockState(belowPos).isReplaceable()) {
            return null;
        }
        
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;
            
            BlockPos neighborPos = belowPos.offset(direction);
            BlockState neighborState = mc.world.getBlockState(neighborPos);

            // TODO: Remove deprecated `.isLiquid()` call
            //noinspection deprecation
            if (!neighborState.isReplaceable() && 
                !neighborState.isLiquid() && 
                neighborState.isSolidBlock(mc.world, neighborPos)) {
                
                Vec3d hitVec = calculateHitVec(belowPos, direction.getOpposite());
                return new BlockPlaceInfo(belowPos, direction.getOpposite(), hitVec);
            }
        }
        
        return null;
    }
    
    private Vec3d calculateHitVec(BlockPos pos, Direction side) {
        Vec3d hitVec = Vec3d.ofCenter(pos);
        
        double offset = 0.5;
        switch (side) {
            case DOWN -> hitVec = hitVec.add(0, -offset, 0);
            case UP -> hitVec = hitVec.add(0, offset, 0);
            case NORTH -> hitVec = hitVec.add(0, 0, -offset);
            case SOUTH -> hitVec = hitVec.add(0, 0, offset);
            case WEST -> hitVec = hitVec.add(-offset, 0, 0);
            case EAST -> hitVec = hitVec.add(offset, 0, 0);
        }
        
        return hitVec;
    }
    
    private void calculateRotations() {
        if (placeInfo == null || mc.player == null) return;
        
        float moveYaw = mc.player.getYaw();
        if (MoveUtility.isMoving()) {
            float forward = MoveUtility.getForward(mc);
            float strafe = MoveUtility.getStrafe(mc);
            
            float yawOffset = 0;
            if (forward > 0) {
                if (strafe > 0) {
                    yawOffset = -45;
                } else if (strafe < 0) {
                    yawOffset = 45;
                }
            } else if (forward < 0) {
                yawOffset = 180;
                if (strafe > 0) {
                    yawOffset += 45;
                } else if (strafe < 0) {
                    yawOffset -= 45;
                }
            } else {
                if (strafe > 0) {
                    yawOffset = -90;
                } else if (strafe < 0) {
                    yawOffset = 90;
                }
            }
            
            moveYaw += yawOffset;
        }
        
        targetYaw = moveYaw + 180;
        targetPitch = 40;
        
        targetPitch = MathHelper.clamp(targetPitch, -90.0f, 90.0f);
    }
    
    private void handleTower() {
        if(mc.player == null) return;
        if (!mc.player.isOnGround()) return;
        
        switch (towerMode.getValue()) {
            case "Vanilla" -> mc.player.jump();
            case "Jump" -> {
                if (mc.player.getVelocity().y < 0.1) {
                    mc.player.jump();
                }
            }
            case "Motion" -> MoveUtility.setMotionY(towerSpeed.getValue());
        }
    }
    
    private void placeBlock() {
        if (mc.player == null || placeInfo == null || blockSlot == -1 || mc.getNetworkHandler() == null || mc.interactionManager == null) return;
        
        BlockHitResult hitResult = new BlockHitResult(
            placeInfo.hitVec,
            placeInfo.side,
            placeInfo.pos,
            false
        );
        
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        
        if (swing.getValue()) {
            if (swingMode.getValue().equals("Client")) {
                mc.player.swingHand(Hand.MAIN_HAND);
            } else {
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
        }
        
        lastPlaceTime = System.currentTimeMillis();
    }
    
    private static class BlockPlaceInfo {
        public final BlockPos pos;
        public final Direction side;
        public final Vec3d hitVec;
        
        public BlockPlaceInfo(BlockPos pos, Direction side, Vec3d hitVec) {
            this.pos = pos;
            this.side = side;
            this.hitVec = hitVec;
        }
    }
}
