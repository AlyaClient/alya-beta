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

package dev.thoq.module.impl.combat.killaura;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class KillauraModule extends Module {

    private final ModeSetting attackMode = new ModeSetting("AttackMode", "Attack mode", "Single", "Single", "Switch", "Multi");
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final NumberSetting<Double> swingDistance = new NumberSetting<>("SwingDistance", "Distance to start swinging", 4.5, 3.0, 1000.0);
    private final NumberSetting<Double> reach = new NumberSetting<>("Reach", "Distance to actually attack from", 4.0, 3.0, 1000.0);
    private final NumberSetting<Integer> cps = new NumberSetting<>("CPS", "Attacks per second", 12, 1, 20);
    private final BooleanSetting noHitDelay = new BooleanSetting("NoHitDelay", "Remove attack delay", false);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", "Check line of sight to target", true);
    private final BooleanSetting movementCorrection = new BooleanSetting("MovementCorrection", "Do not attack impossibly", true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate to targets", true);
    private final ModeSetting rotationMode = new ModeSetting("RotationMode", "Rotation mode", "Smooth", "Snap", "Smooth", "Linear");
    private final BooleanSetting serverSideRotations = new BooleanSetting("ServerSideRotations", "Server-side rotations only", true);
    private final NumberSetting<Double> rotationSpeed = new NumberSetting<>("RotationSpeed", "Rotation smoothness", 3.0, 1.0, 10.0);
    private final BooleanSetting gcd = new BooleanSetting("GCD", "Greatest Common Divisor stuff idfk", true);
    private final BooleanSetting autoBlock = new BooleanSetting("AutoBlock", "Auto block with sword", false);
    private final ModeSetting autoBlockMode = new ModeSetting("AutoBlockMode", "AutoBlock mode", "Vanilla", "Vanilla", "Packet");
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

        cps.setVisibilityCondition(() -> !noHitDelay.getValue());
        rotationMode.setVisibilityCondition(rotate::getValue);
        serverSideRotations.setVisibilityCondition(rotate::getValue);
        rotationSpeed.setVisibilityCondition(() -> rotate.getValue() && rotationMode.getValue().equals("Smooth"));
        autoBlockMode.setVisibilityCondition(autoBlock::getValue);
        gcd.setVisibilityCondition(rotate::getValue);

        addSetting(attackMode);
        addSetting(targetMode);
        addSetting(swingDistance);
        addSetting(reach);
        addSetting(cps);
        addSetting(rotate);
        addSetting(rotationMode);
        addSetting(serverSideRotations);
        addSetting(rotationSpeed);
        addSetting(autoBlock);
        addSetting(autoBlockMode);
        addSetting(movementCorrection);
        addSetting(gcd);
        addSetting(raycast);
        addSetting(noHitDelay);
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || mc.world == null || !event.isPre()) return;

        findTargets();
        selectTarget();
        handleAutoBlock();

        if(targets.isEmpty()) {
            if(isBlocking)
                stopBlocking();
            return;
        }

        if(rotate.getValue() && currentTarget != null) {
            calculateRotations();

            if(serverSideRotations.getValue()) {
                event.setYaw(targetYaw);
                event.setPitch(targetPitch);
            } else {
                mc.player.setYaw(targetYaw);
                mc.player.setPitch(targetPitch);
            }
        }

        if(canAttack() && isValidHit())
            performAttack();
    };

    private void findTargets() {
        targets.clear();

        if(mc.player == null || mc.world == null) return;

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class, mc.player.getBoundingBox().expand(1000), this::isValidTarget);
        entities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));

        for(Entity entity : entities) {
            if(raycast.getValue() && !canSeeTarget(entity)) continue;
            targets.add(entity);
        }
    }

    private boolean canSeeTarget(Entity target) {
        if(mc.player == null || mc.world == null) return false;

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(target);

        RaycastContext context = new RaycastContext(
                playerEyes,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
        );

        BlockHitResult result = mc.world.raycast(context);
        return result.getType() == HitResult.Type.MISS;
    }

    private boolean isValidTarget(Entity entity) {
        if(mc.player == null) return false;
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
            case "Single" -> currentTarget = targets.isEmpty() ? null : targets.getFirst();
            case "Switch" -> {
                if(switchTimer++ >= 20 && targets.size() > 1) {
                    switchTimer = 0;
                    int currentIndex = targets.indexOf(currentTarget);
                    currentTarget = targets.get((currentIndex + 1) % targets.size());
                } else if(currentTarget == null || !targets.contains(currentTarget)) {
                    currentTarget = targets.isEmpty() ? null : targets.getFirst();
                }
            }
            case "Multi" -> currentTarget = targets.isEmpty() ? null : targets.getFirst();
        }
    }

    private float[] applyGCDCorrection(float yaw, float pitch) {
        if(!gcd.getValue()) {
            return new float[]{yaw, pitch};
        }

        float gcd = getGCD();

        float correctedYaw = Math.round(yaw / gcd) * gcd;
        float correctedPitch = Math.round(pitch / gcd) * gcd;

        correctedPitch = MathHelper.clamp(correctedPitch, -90.0F, 90.0F);

        return new float[]{correctedYaw, correctedPitch};
    }

    private float getGCD() {
        float sensitivity = mc.options.getMouseSensitivity().getValue().floatValue();
        return sensitivity * 0.6F + 0.2F;
    }

    private void calculateRotations() {
        if(currentTarget == null || mc.player == null) return;

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
                    float jitterPitch = (random.nextFloat() - 0.5f);

                    lastYaw = targetYaw + jitterYaw;
                    lastPitch = MathHelper.clamp(targetPitch + jitterPitch, -90.0F, 90.0F);
                }
            }
        }

        float[] correctedRotations = applyGCDCorrection(lastYaw, lastPitch);
        targetYaw = correctedRotations[0];
        targetPitch = correctedRotations[1];
    }

    private boolean isValidHit() {
        if(currentTarget == null || mc.player == null) return false;
        if(!movementCorrection.getValue()) return true;

        if(attackMode.getValue().equals("Multi")) {
            return targets.stream().anyMatch(this::canHitTarget);
        }

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(currentTarget);

        Vec3d direction = targetPos.subtract(playerEyes).normalize();

        float currentYaw = serverSideRotations.getValue() ? targetYaw : mc.player.getYaw();
        float currentPitch = serverSideRotations.getValue() ? targetPitch : mc.player.getPitch();

        Vec3d lookDirection = getLookDirection(currentYaw, currentPitch);

        double dotProduct = direction.dotProduct(lookDirection);
        double angleRadians = Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));
        double angleDegrees = Math.toDegrees(angleRadians);

        return angleDegrees <= 90.0;
    }

    private boolean canHitTarget(Entity target) {
        if(target == null || mc.player == null) return false;

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(target);
        Vec3d direction = targetPos.subtract(playerEyes).normalize();

        float currentYaw = serverSideRotations.getValue() ? targetYaw : mc.player.getYaw();
        float currentPitch = serverSideRotations.getValue() ? targetPitch : mc.player.getPitch();

        Vec3d lookDirection = getLookDirection(currentYaw, currentPitch);
        double dotProduct = direction.dotProduct(lookDirection);
        double angleRadians = Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));
        double angleDegrees = Math.toDegrees(angleRadians);

        return angleDegrees <= 90.0;
    }

    private Vec3d getLookDirection(float yaw, float pitch) {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        float cosYaw = MathHelper.cos(-yawRad - (float)Math.PI);
        float sinYaw = MathHelper.sin(-yawRad - (float)Math.PI);
        float cosPitch = -MathHelper.cos(-pitchRad);
        float sinPitch = MathHelper.sin(-pitchRad);

        return new Vec3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }

    private Vec3d getTargetPosition(Entity target) {
        return target.getBoundingBox().getCenter();
    }

    private boolean canAttack() {
        if(targets.isEmpty() || mc.player == null) return false;

        if(currentTarget != null && mc.player.distanceTo(currentTarget) > reach.getValue()) {
            return false;
        }

        if(noHitDelay.getValue()) return true;

        long currentTime = System.currentTimeMillis();
        long baseDelay = 1000L / cps.getValue();
        double randomFactor = 0.8 + (random.nextDouble() * 0.4);
        long actualDelay = Math.round(baseDelay * randomFactor);
        actualDelay = Math.max(actualDelay, 50L);

        return currentTime - lastAttackTime >= actualDelay;
    }

    private void performAttack() {
        if(mc.player == null || mc.interactionManager == null) return;

        boolean wasBlocking = isBlocking;
        if(isBlocking) stopBlocking();

        if(attackMode.getValue().equals("Multi")) {
            for(Entity target : targets) {
                if(mc.player.distanceTo(target) <= reach.getValue()) {
                    attackEntity(target);
                }
            }
        } else if(currentTarget != null && mc.player.distanceTo(currentTarget) <= reach.getValue()) {
            attackEntity(currentTarget);
        }

        if(!noHitDelay.getValue()) {
            lastAttackTime = System.currentTimeMillis();
        }

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
        if(target == null || mc.interactionManager == null || mc.player == null) return;

        double distanceToTarget = mc.player.distanceTo(target);

        if(distanceToTarget <= swingDistance.getValue()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }

        if(distanceToTarget <= reach.getValue()) {
            mc.interactionManager.attackEntity(mc.player, target);
        }
    }

    private void handleAutoBlock() {
        if(mc.player == null) return;
        if(!autoBlock.getValue() || targets.isEmpty()) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(!canBlock()) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(!isBlocking && mc.player.getAttackCooldownProgress(0.0f) >= 0.9f) {
            startBlocking();
        }
    }

    private boolean canBlock() {
        if(mc.player == null) return false;
        if(mc.player.getMainHandStack().isEmpty()) return false;

        return mc.player.getMainHandStack().getItem().toString().toLowerCase().contains("sword");
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
        if(!isBlocking || mc.player == null) return;

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