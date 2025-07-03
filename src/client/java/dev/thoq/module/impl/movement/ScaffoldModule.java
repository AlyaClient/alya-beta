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

package dev.thoq.module.impl.movement;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.event.impl.PacketSendEvent;
import dev.thoq.mixin.client.accessors.MinecraftClientAccessor;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class ScaffoldModule extends Module {

    private final BooleanSetting sprint = new BooleanSetting("Sprint", "Allow sprinting while scaffolding", true);
    private final BooleanSetting swing = new BooleanSetting("Swing", "Swing arm when placing blocks", true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate towards block placement", true);
    private final BooleanSetting tower = new BooleanSetting("Tower", "Enable tower mode when jumping", true);

    private final NumberSetting<Integer> searchRange = new NumberSetting<>("Search Range", "Block search radius", 3, 1, 6);
    private final NumberSetting<Integer> bruteForceRayCastIntensity = new NumberSetting<>("Brute Force Intensity", "Intensity of rotation calculation", 5, 1, 10);
    private final NumberSetting<Double> towerSpeed = new NumberSetting<>("Tower Speed", "Speed for tower mode", 0.42, 0.1, 1.0);

    private final ModeSetting switchItemMode = new ModeSetting("Switch Item Mode", "How to switch items", "Client", "Client", "Server");
    private final ModeSetting swingMode = new ModeSetting("Swing Mode", "How to swing arm", "Client", "Client", "Server");
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "How to calculate rotations", "Enum", "Enum", "Brute Force RayCast");
    private final ModeSetting towerMode = new ModeSetting("Tower Mode", "Tower movement method", "Vanilla", "Vanilla", "BlocksMC", "Verus", "Vulcan");

    private BlockCache blockCache, lastBlockCache;
    private int startSlot, slot, lastSlot;
    private long lastPlaceTime = 0;
    private float yaw, pitch;

    public ScaffoldModule() {
        super("Scaffold", "Automatically bridges for you", ModuleCategory.WORLD);

        addSetting(sprint);
        addSetting(searchRange);
        addSetting(rotate);
        addSetting(rotationMode.setVisibilityCondition(rotate::getValue));
        addSetting(bruteForceRayCastIntensity.setVisibilityCondition(() -> rotate.getValue() && rotationMode.getValue().equals("Brute Force RayCast")));
        addSetting(switchItemMode);
        addSetting(swing);
        addSetting(swingMode.setVisibilityCondition(swing::getValue));
        addSetting(tower);
        addSetting(towerMode.setVisibilityCondition(tower::getValue));
        addSetting(towerSpeed.setVisibilityCondition(() -> tower.getValue() && towerMode.getValue().equals("Vanilla")));
    }

    @Override
    protected void onEnable() {
        if(mc.player == null) return;
        if(mc.player.getInventory() == null) return;

        startSlot = mc.player.getInventory().getSelectedSlot();
        slot = lastSlot = -1;
        blockCache = lastBlockCache = null;

        yaw = getYaw() + 180;
        pitch = 80;
    }

    @Override
    protected void onDisable() {
        if(mc.player == null) return;

        if(mc.player.getInventory().getSelectedSlot() != startSlot || lastSlot != startSlot) {
            if(switchItemMode.getValue().equals("Client")) {
                mc.player.getInventory().setSelectedSlot(startSlot);
            } else if(mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(startSlot));
            }
        }
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || mc.world == null) return;

        slot = findBlockSlot();

        if(sprint.getValue() && MoveUtility.isMoving()) {
            mc.player.setSprinting(true);
            MoveUtility.setSpeed(0.2873);
        } else {
            mc.player.setSprinting(false);
            MoveUtility.setSpeed(0.2);
        }

        if(slot != -1) {
            if(switchItemMode.getValue().equals("Client") && mc.player.getInventory().getSelectedSlot() != slot) {
                mc.player.getInventory().setSelectedSlot(slot);
            }
            if(switchItemMode.getValue().equals("Server") && lastSlot != slot && mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                lastSlot = slot;
            }
        }

        if(event.isPre()) {
            blockCache = getBlockCache();
            if(blockCache != null) lastBlockCache = blockCache;

            if(rotate.getValue() && lastBlockCache != null) {
                updateRotations();

                float sensitivityMultiplier = 0.5f;
                float fixedYaw = yaw - yaw % sensitivityMultiplier;
                float fixedPitch = pitch - pitch % sensitivityMultiplier;

                event.setYaw(fixedYaw);
                event.setPitch(fixedPitch);
            }

            if(mc.player.isOnGround()) {
                mc.player.setVelocity(mc.player.getVelocity().x * 0.91f, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.91f);
            }

            if(tower.getValue() && mc.options.jumpKey.isPressed() && blockCache != null) {
                handleTower();
            }
        }

        if(event.isPost() && lastBlockCache != null && slot != -1) {
            placeBlock();
        }
    };

    @SuppressWarnings("unused")
    private final IEventListener<PacketSendEvent> packetSendEvent = event -> {
        if(event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            if(switchItemMode.getValue().equals("Server")) {
                startSlot = packet.getSelectedSlot();
                event.cancel();
            }
        }
    };

    private void placeBlock() {
        if(blockCache == null || lastBlockCache == null || slot == -1 || mc.interactionManager == null) return;

        ((MinecraftClientAccessor) mc).setItemUseCooldown(0);

        ItemStack heldItem = mc.player.getInventory().getStack(slot);
        if(heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem)) {
            return;
        }

        BlockHitResult hitResult = new BlockHitResult(
                getHitVec(),
                lastBlockCache.getEnumFacing(),
                lastBlockCache.getBlockPos(),
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
        }

        blockCache = null;
    }

    private void updateRotations() {
        switch(rotationMode.getValue()) {
            case "Enum":
                this.yaw = getDirectionYaw(lastBlockCache.getEnumFacing()) - 180;
                this.pitch = 77;
                break;
            case "Brute Force RayCast":
                float bestYaw = getYaw() + 180;
                float bestPitch = 80;

                for(float testYaw = getYaw() - 180; testYaw < getYaw() + 180; testYaw += bruteForceRayCastIntensity.getValue()) {
                    for(float testPitch = 90; testPitch > -90; testPitch -= bruteForceRayCastIntensity.getValue()) {
                        Vec3d eyePos = Objects.requireNonNull(mc.player).getEyePos();
                        float yawRad = (float) Math.toRadians(testYaw);
                        float pitchRad = (float) Math.toRadians(testPitch);

                        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
                        double y = -Math.sin(pitchRad);
                        double z = Math.cos(yawRad) * Math.cos(pitchRad);

                        Vec3d lookVec = new Vec3d(x, y, z).normalize().multiply(searchRange.getValue());
                        Vec3d endPos = eyePos.add(lookVec);

                        RaycastContext context = new RaycastContext(
                                eyePos,
                                endPos,
                                RaycastContext.ShapeType.OUTLINE,
                                RaycastContext.FluidHandling.NONE,
                                mc.player
                        );

                        BlockHitResult result = mc.world.raycast(context);

                        if(result != null && result.getBlockPos().equals(lastBlockCache.getBlockPos())) {
                            bestYaw = testYaw;
                            bestPitch = testPitch;
                            break;
                        }
                    }
                }

                this.yaw = bestYaw;
                this.pitch = bestPitch;
                break;
        }
    }

    private float getDirectionYaw(Direction direction) {
        switch(direction) {
            case NORTH: return 0;
            case SOUTH: return 180;
            case WEST: return 90;
            case EAST: return 270;
            default: return getYaw();
        }
    }

    private Vec3d getHitVec() {
        double x = lastBlockCache.getBlockPos().getX() + 0.5;
        double y = lastBlockCache.getBlockPos().getY() + 0.5;
        double z = lastBlockCache.getBlockPos().getZ() + 0.5;

        if(lastBlockCache.getEnumFacing() != Direction.UP && lastBlockCache.getEnumFacing() != Direction.DOWN) {
            y += 0.5;
        } else {
            x += 0.3;
            z += 0.3;
        }

        if(lastBlockCache.getEnumFacing() == Direction.SOUTH || lastBlockCache.getEnumFacing() == Direction.NORTH) {
            x += 0.15;
        }
        if(lastBlockCache.getEnumFacing() == Direction.EAST || lastBlockCache.getEnumFacing() == Direction.WEST) {
            z += 0.15;
        }

        return new Vec3d(x, y, z);
    }

    private void handleTower() {
        if(mc.player == null) return;

        switch(towerMode.getValue()) {
            case "Vanilla":
                MoveUtility.setMotionY(towerSpeed.getValue());
                break;
            case "BlocksMC":
                if(mc.options.jumpKey.isPressed()) {
                    MoveUtility.setMotionY(0.42f);
                }
                if(MoveUtility.isMoving()) {
                    MoveUtility.setMotionY(0.15f);
                }
                break;
            case "Verus":
                if(mc.player.isOnGround()) {
                    MoveUtility.setMotionY(-0.1f);
                    mc.player.jump();
                } else {
                    MoveUtility.setMotionY(1f);
                }
                break;
            case "Vulcan":
                if(mc.options.jumpKey.isPressed()) {
                    MoveUtility.setMotionY(0.2f);
                }
                break;
        }
    }

    private BlockCache getBlockCache() {
        if(mc.player == null || mc.world == null) return null;

        BlockPos belowBlockPos = BlockPos.ofFloored(mc.player.getPos().subtract(0, 1, 0));
        if(!mc.world.getBlockState(belowBlockPos).isAir()) return null;

        for(int x = 0; x < searchRange.getValue(); x++) {
            for(int z = 0; z < searchRange.getValue(); z++) {
                BlockCache blockCache = findBlockCacheAtPosition(belowBlockPos, x, z);
                if(blockCache != null) return blockCache;
            }
        }
        return null;
    }

    private BlockCache findBlockCacheAtPosition(BlockPos belowBlockPos, int x, int z) {
        for(int i = 1; i > -3; i -= 2) {
            final BlockPos blockPos = belowBlockPos.add(x * i, 0, z * i);
            if(mc.world.getBlockState(blockPos).isAir()) {
                BlockCache blockCache = checkSurroundingBlocks(blockPos);
                if(blockCache != null) return blockCache;
            }
        }
        return null;
    }

    private BlockCache checkSurroundingBlocks(BlockPos blockPos) {
        for(final Direction direction : Direction.values()) {
            final BlockPos neighborPos = blockPos.offset(direction);
            final BlockState blockState = mc.world.getBlockState(neighborPos);

            if(!blockState.isAir() && !blockState.isLiquid() && blockState.isSolidBlock(mc.world, neighborPos)) {
                return new BlockCache(neighborPos, direction.getOpposite());
            }
        }
        return null;
    }

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

    private float getYaw() {
        return mc.player != null ? mc.player.getYaw() : 0;
    }

    public static final class BlockCache {
        private final BlockPos blockPos;
        private final Direction enumFacing;

        public BlockCache(final BlockPos blockPos, final Direction enumFacing) {
            this.blockPos = blockPos;
            this.enumFacing = enumFacing;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public Direction getEnumFacing() {
            return enumFacing;
        }
    }
}
