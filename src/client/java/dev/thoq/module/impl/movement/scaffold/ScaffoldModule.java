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

package dev.thoq.module.impl.movement.scaffold;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.mixin.client.accessors.MinecraftClientAccessor;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
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
    private final BooleanSetting tower = new BooleanSetting("Tower", "Enable tower mode when jumping", false);

    private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Placement delay in milliseconds", 100, 0, 500);
    private final NumberSetting<Double> towerSpeed = new NumberSetting<>("Tower Speed", "Speed for tower mode", 0.42, 0.1, 1.0);

    private final ModeSetting swingMode = new ModeSetting("Swing Mode", "How to swing arm", "Client", "Client", "Server");
    private final ModeSetting towerMode = new ModeSetting("Tower Mode", "Tower movement method", "Vanilla", "Vanilla", "Motion");

    private int originalSlot = -1;
    private int blockSlot = -1;
    private long lastPlaceTime = 0;
    private float targetYaw, targetPitch;

    private BlockPos targetPos;
    private Direction targetSide;
    private Vec3d targetHitVec;

    public ScaffoldModule() {
        super("Scaffold", "Automatically places blocks beneath you", ModuleCategory.MOVEMENT);

        addSetting(sprint);
        addSetting(swing);
        addSetting(rotate);
        addSetting(tower);
        addSetting(delay);
        addSetting(swingMode.setVisibilityCondition(swing::getValue));
        addSetting(towerMode.setVisibilityCondition(tower::getValue));
        addSetting(towerSpeed.setVisibilityCondition(() -> tower.getValue() && towerMode.getValue().equals("Motion")));
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) {
            originalSlot = mc.player.getInventory().getSelectedSlot();
        }
    }

    @Override
    protected void onDisable() {
        if(mc.player != null && originalSlot != -1) {
            mc.player.getInventory().setSelectedSlot(originalSlot);
        }
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || mc.world == null) return;

        blockSlot = findBlockSlot();
        if(blockSlot == -1) return;

        if(mc.player.getInventory().getSelectedSlot() != blockSlot) {
            mc.player.getInventory().setSelectedSlot(blockSlot);
        }

        if(sprint.getValue() && MoveUtility.isMoving()) {
            mc.player.setSprinting(true);
        }

        findPlacementPosition();

        if(event.isPre() && targetPos != null) {
            if(rotate.getValue()) {
                calculateRotations();
                event.setYaw(targetYaw);
                event.setPitch(targetPitch);
            }

            if(tower.getValue() && mc.options.jumpKey.isPressed()) {
                handleTower();
            }
        }

        if(event.isPost() && targetPos != null && System.currentTimeMillis() - lastPlaceTime >= delay.getValue()) {
            placeBlock();
        }
    };

    private int findBlockSlot() {
        if(mc.player == null) return -1;

        for(int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                if(blockItem.getBlock() != Blocks.AIR &&
                        !blockItem.getBlock().getDefaultState().isLiquid()) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void findPlacementPosition() {
        if(mc.player == null || mc.world == null) {
            targetPos = null;
            return;
        }

        BlockPos playerPos = BlockPos.ofFloored(mc.player.getPos());
        BlockPos belowPos = playerPos.down();

        if(!mc.world.getBlockState(belowPos).isReplaceable()) {
            targetPos = null;
            return;
        }

        for(Direction direction : Direction.values()) {
            if(direction == Direction.UP) continue;

            BlockPos neighborPos = belowPos.offset(direction);
            BlockState neighborState = mc.world.getBlockState(neighborPos);

            if(!neighborState.isReplaceable() &&
                    !neighborState.isLiquid() &&
                    neighborState.isSolidBlock(mc.world, neighborPos)) {

                targetPos = belowPos;
                targetSide = direction.getOpposite();
                targetHitVec = Vec3d.ofCenter(neighborPos).add(
                        targetSide.getOffsetX() * 0.5,
                        targetSide.getOffsetY() * 0.5,
                        targetSide.getOffsetZ() * 0.5
                );
                return;
            }
        }

        targetPos = null;
    }

    private void calculateRotations() {
        if(targetPos == null || mc.player == null) return;

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetVec = targetHitVec != null ? targetHitVec : Vec3d.ofCenter(targetPos);

        Vec3d diff = targetVec.subtract(playerEyes);
        double distance = Math.sqrt(diff.x * diff.x + diff.z * diff.z);

        targetYaw = (float) (Math.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90.0f;
        targetPitch = (float) (-(Math.atan2(diff.y, distance) * 180.0 / Math.PI));

        targetPitch = MathHelper.clamp(targetPitch, -90.0f, 90.0f);
    }

    private void handleTower() {
        if(mc.player == null || !mc.player.isOnGround()) return;

        switch(towerMode.getValue()) {
            case "Vanilla" -> mc.player.jump();
            case "Motion" -> MoveUtility.setMotionY(towerSpeed.getValue());
        }
    }

    private void placeBlock() {
        if(mc.player == null || targetPos == null || targetSide == null ||
                blockSlot == -1 || mc.interactionManager == null) return;

        ((MinecraftClientAccessor) mc).setItemUseCooldown(0);

        ItemStack heldItem = mc.player.getInventory().getStack(blockSlot);
        if(heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem)) {
            return;
        }

        if(!mc.world.getBlockState(targetPos).isReplaceable()) {
            targetPos = null;
            return;
        }

        Vec3d hitVec = targetHitVec != null ? targetHitVec : Vec3d.ofCenter(targetPos.offset(targetSide));
        BlockHitResult hitResult = new BlockHitResult(
                hitVec,
                targetSide,
                targetPos.offset(targetSide),
                false
        );

        ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);

        if(result.isAccepted()) {
            if(swing.getValue()) {
                if(swingMode.getValue().equals("Client")) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                } else if(mc.getNetworkHandler() != null) {
                    mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                }
            }

            lastPlaceTime = System.currentTimeMillis();
        }

        targetPos = null;
    }
}