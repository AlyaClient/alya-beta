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

package dev.thoq.module.impl.combat.killaura;

// Add this import at the top with your other imports
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class KillauraModule extends Module {

    private final ModeSetting attackMode = new ModeSetting("AttackMode", "Attack mode", "Single", "Single", "Switch", "Multi");
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final NumberSetting<Double> range = new NumberSetting<>("Range", "Attack range", 4.0, 3.0, 6.0);
    private final NumberSetting<Integer> cps = new NumberSetting<>("CPS", "Attacks per second", 12, 1, 20);

    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate to targets", true);
    private final ModeSetting rotationMode = new ModeSetting("RotationMode", "Rotation mode", "Smooth", "Snap", "Smooth", "Linear");
    private final BooleanSetting serverSideRotations = new BooleanSetting("ServerSideRotations", "Server-side rotations only", true);
    private final NumberSetting<Double> rotationSpeed = new NumberSetting<>("RotationSpeed", "Rotation smoothness", 3.0, 1.0, 10.0);
    private final BooleanSetting gcdMovementCorrection = new BooleanSetting("GCD Movement Correction", "Corrects rotation movements to GCD values", true);

    private final BooleanSetting autoBlock = new BooleanSetting("AutoBlock", "Auto block with sword", false);
    private final ModeSetting autoBlockMode = new ModeSetting("AutoBlockMode", "AutoBlock mode", "Vanilla", "Vanilla", "Fake", "Packet");

    private final BooleanSetting showParticles = new BooleanSetting("ShowParticles", "Show attack particles", false);

    private final List<Entity> targets = new ArrayList<>();
    private Entity currentTarget;
    private int switchTimer = 0;
    private boolean isBlocking = false;

    private long lastAttackTime = 0;
    private final Random random = new Random();

    private float lastYaw = 0f;
    private float lastPitch = 0f;
    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private long lastRotationTime = 0;

    public KillauraModule() {
        super("Killaura", "Automatically attack entities", ModuleCategory.COMBAT);

        rotationMode.setVisibilityCondition(() -> rotate.getValue());
        serverSideRotations.setVisibilityCondition(() -> rotate.getValue());
        rotationSpeed.setVisibilityCondition(() -> rotate.getValue() && rotationMode.getValue().equals("Smooth"));
        autoBlockMode.setVisibilityCondition(() -> autoBlock.getValue());
        gcdMovementCorrection.setVisibilityCondition(() -> rotate.getValue());

        addSetting(attackMode);
        addSetting(targetMode);
        addSetting(range);
        addSetting(cps);
        addSetting(rotate);
        addSetting(rotationMode);
        addSetting(serverSideRotations);
        addSetting(rotationSpeed);
        addSetting(gcdMovementCorrection);
        addSetting(autoBlock);
        addSetting(autoBlockMode);
        addSetting(showParticles);
    }

    @Override
    protected void onPreTick() {
        if(mc.player == null || mc.world == null) return;

        findTargets();
        selectTarget();

        if(currentTarget == null) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(rotate.getValue()) {
            handleRotations();
        }

        if(canAttack()) {
            performAttack();
        }
    }

    @Override
    protected void onTick() {
        if(mc.player == null || mc.world == null) return;

        handleAutoBlock();
    }

    private void findTargets() {
        targets.clear();

        if(mc.player == null || mc.world == null) return;

        Box searchBox = new Box(
                mc.player.getX() - range.getValue(),
                mc.player.getY() - range.getValue(),
                mc.player.getZ() - range.getValue(),
                mc.player.getX() + range.getValue(),
                mc.player.getY() + range.getValue(),
                mc.player.getZ() + range.getValue()
        );

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class, searchBox, this::isValidTarget);
        entities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));
        targets.addAll(entities);
    }

    private boolean isValidTarget(Entity entity) {
        if(!(entity instanceof LivingEntity) || entity == mc.player || !entity.isAlive()) {
            return false;
        }

        if(entity.isInvisible() && !mc.player.canSee(entity)) {
            return false;
        }

        return switch(targetMode.getValue()) {
            case "Players" -> entity instanceof PlayerEntity;
            case "Passive" -> entity instanceof PassiveEntity;
            case "Hostile" -> entity instanceof HostileEntity;
            case "All" ->
                    entity instanceof PlayerEntity || entity instanceof PassiveEntity || entity instanceof HostileEntity;
            default -> false;
        };
    }

    private void selectTarget() {
        switch(attackMode.getValue()) {
            case "Single" -> {
                currentTarget = targets.isEmpty() ? null : targets.get(0);
            }
            case "Switch" -> {
                if(switchTimer++ >= 20 && targets.size() > 1) {
                    switchTimer = 0;
                    int currentIndex = targets.indexOf(currentTarget);
                    currentTarget = targets.get((currentIndex + 1) % targets.size());
                } else if(currentTarget == null || !targets.contains(currentTarget)) {
                    currentTarget = targets.isEmpty() ? null : targets.get(0);
                }
            }
            case "Multi" -> {
                currentTarget = targets.isEmpty() ? null : targets.get(0);
            }
        }
    }

    private float[] applyGCDCorrection(float yaw, float pitch) {
        if(!gcdMovementCorrection.getValue()) {
            return new float[]{yaw, pitch};
        }

        float gcd = getGCD();

        float correctedYaw = (float) (Math.round(yaw / gcd) * gcd);
        float correctedPitch = (float) (Math.round(pitch / gcd) * gcd);

        correctedPitch = MathHelper.clamp(correctedPitch, -90.0F, 90.0F);

        return new float[]{correctedYaw, correctedPitch};
    }

    private float getGCD() {

        float sensitivity = mc.options.getMouseSensitivity().getValue().floatValue();
        return sensitivity * 0.6F + 0.2F;
    }

    // Update the handleRotations method
    private void handleRotations() {
        if(currentTarget == null) return;

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(currentTarget);

        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        targetPitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));
        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        switch(rotationMode.getValue()) {
            case "Snap" -> {
                lastYaw = targetYaw;
                lastPitch = targetPitch;
            }
            case "Smooth" -> {
                float smoothness = rotationSpeed.getValue().floatValue();
                float yawDelta = MathHelper.wrapDegrees(targetYaw - lastYaw);
                float pitchDelta = targetPitch - lastPitch;

                lastYaw += yawDelta / smoothness;
                lastPitch += pitchDelta / smoothness;
                lastPitch = MathHelper.clamp(lastPitch, -90.0F, 90.0F);
            }
            case "Linear" -> {
                long currentTime = System.currentTimeMillis();
                if(currentTime - lastRotationTime > 50) {
                    lastRotationTime = currentTime;

                    float jitterYaw = (random.nextFloat() - 0.5f) * 2.0f;
                    float jitterPitch = (random.nextFloat() - 0.5f) * 1.0f;

                    lastYaw = targetYaw + jitterYaw;
                    lastPitch = MathHelper.clamp(targetPitch + jitterPitch, -90.0F, 90.0F);
                }
            }
        }

        float[] correctedRotations = applyGCDCorrection(lastYaw, lastPitch);
        lastYaw = correctedRotations[0];
        lastPitch = correctedRotations[1];

        if (serverSideRotations.getValue()) {
            sendRotationPacket(lastYaw, lastPitch);
        } else {
            mc.player.setYaw(lastYaw);
            mc.player.setPitch(lastPitch);
        }
    }

    private void sendRotationPacket(float yaw, float pitch) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        
        PlayerMoveC2SPacket rotationPacket = new PlayerMoveC2SPacket.LookAndOnGround(
            yaw,
            pitch,
            mc.player.isOnGround(),
            mc.player.horizontalCollision
        );
        
        mc.getNetworkHandler().sendPacket(rotationPacket);
    }

    private Vec3d getTargetPosition(Entity target) {

        return target.getBoundingBox().getCenter();
    }

    private boolean canAttack() {
        if(currentTarget == null || mc.player == null) return false;

        long currentTime = System.currentTimeMillis();

        long baseDelay = 1000L / cps.getValue();

        double randomFactor = 0.8 + (random.nextDouble() * 0.4);
        long actualDelay = Math.round(baseDelay * randomFactor);

        actualDelay = Math.max(actualDelay, 50L);

        return currentTime - lastAttackTime >= actualDelay;
    }

    private void performAttack() {
        if(currentTarget == null || mc.player == null || mc.interactionManager == null) return;

        if(!isValidTarget(currentTarget) || mc.player.distanceTo(currentTarget) > range.getValue()) {
            return;
        }

        boolean wasBlocking = isBlocking;
        if(isBlocking) stopBlocking();

        attackEntity(currentTarget);

        if(attackMode.getValue().equals("Multi") && targets.size() > 1) {
            int maxTargets = Math.min(targets.size(), 3);
            for(int i = 1; i < maxTargets; i++) {
                if(mc.player.distanceTo(targets.get(i)) <= range.getValue()) {
                    attackEntity(targets.get(i));
                }
            }
        }

        lastAttackTime = System.currentTimeMillis();

        if(wasBlocking && autoBlock.getValue()) {

            new Thread(() -> {
                try {
                    Thread.sleep(random.nextInt(50) + 25);
                    if(isEnabled() && autoBlock.getValue()) {
                        startBlocking();
                    }
                } catch(InterruptedException ignored) {
                }
            }).start();
        }
    }

    private void attackEntity(Entity target) {
        if(target == null || mc.interactionManager == null) return;

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if(showParticles.getValue()) {
            mc.player.onAttacking(target);
        }
    }

    private void handleAutoBlock() {
        if(!autoBlock.getValue() || currentTarget == null) {
            if(isBlocking) stopBlocking();
            return;
        }

        ItemStack mainHand = mc.player.getMainHandStack();
        if(!canBlock(mainHand)) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(!isBlocking && mc.player.getAttackCooldownProgress(0.0f) >= 0.9f) {
            startBlocking();
        }
    }

    private boolean canBlock(ItemStack stack) {

        return false;
    }

    private void startBlocking() {
        if(isBlocking || mc.player == null) return;

        switch(autoBlockMode.getValue()) {
            case "Vanilla" -> mc.options.useKey.setPressed(true);
            case "Packet" -> {
                ItemStack mainHand = mc.player.getMainHandStack();
                if(!mainHand.isEmpty()) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch()));
                }
            }
            case "Fake" -> {
            }
        }

        isBlocking = true;
    }

    private void stopBlocking() {
        if(!isBlocking) return;

        switch(autoBlockMode.getValue()) {
            case "Vanilla" -> mc.options.useKey.setPressed(false);
            case "Packet" -> mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    Direction.DOWN
            ));
            default -> throw new IllegalStateException("Unexpected value: " + autoBlockMode.getValue());
        }

        isBlocking = false;
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }
        lastAttackTime = 0;
        switchTimer = 0;
        isBlocking = false;
    }

    @Override
    protected void onDisable() {
        if(isBlocking) {
            stopBlocking();
        }

        targets.clear();
        currentTarget = null;
        switchTimer = 0;
        isBlocking = false;
    }
}