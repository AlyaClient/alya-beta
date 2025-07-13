/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.module.impl.combat;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AimAssistModule extends Module {
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final ModeSetting sortMode = new ModeSetting("Sort", "Target sorting", "Distance", "Distance", "Health", "Angle", "Crosshair");
    private final NumberSetting<Double> range = new NumberSetting<>("Range", "Maximum target distance", 6.0, 1.0, 10.0);
    private final NumberSetting<Double> fov = new NumberSetting<>("FOV", "Field of view", 90.0, 15.0, 360.0);
    private final BooleanSetting targetPlayers = new BooleanSetting("TargetPlayers", "Target players", true);
    private final BooleanSetting targetHostile = new BooleanSetting("TargetHostile", "Target hostile mobs", false);
    private final BooleanSetting targetPassive = new BooleanSetting("TargetPassive", "Target passive mobs", false);
    private final BooleanSetting targetInvisible = new BooleanSetting("TargetInvisible", "Target invisible entities", false);
    private final BooleanSetting targetTeam = new BooleanSetting("TargetTeam", "Target team members", false);
    private final BooleanSetting prioritizeAttacking = new BooleanSetting("PrioritizeAttacking", "Prioritize entities attacking you", true);
    private final BooleanSetting aimOnClick = new BooleanSetting("AimOnClick", "Only aim when clicking", true);
    private final BooleanSetting aimOnTarget = new BooleanSetting("AimOnTarget", "Only aim when looking near target", true);
    private final BooleanSetting smoothAim = new BooleanSetting("SmoothAim", "Smooth aiming", true);
    private final NumberSetting<Double> horizontalSpeed = new NumberSetting<>("HSpeed", "Horizontal aim speed", 2.0, 0.1, 10.0);
    private final NumberSetting<Double> verticalSpeed = new NumberSetting<>("VSpeed", "Vertical aim speed", 2.0, 0.1, 10.0);
    private final NumberSetting<Double> minAimSpeed = new NumberSetting<>("MinSpeed", "Minimum aim speed", 0.5, 0.1, 5.0);
    private final NumberSetting<Double> maxAimSpeed = new NumberSetting<>("MaxSpeed", "Maximum aim speed", 3.0, 0.5, 10.0);
    private final BooleanSetting randomizeSpeed = new BooleanSetting("RandomSpeed", "Randomize aim speed", true);
    private final BooleanSetting aimPrediction = new BooleanSetting("Prediction", "Predict target movement", true);
    private final NumberSetting<Double> predictionStrength = new NumberSetting<>("PredictStrength", "Prediction strength", 1.0, 0.1, 3.0);
    private final BooleanSetting rayTrace = new BooleanSetting("RayTrace", "Check line of sight", true);
    private final BooleanSetting lockView = new BooleanSetting("LockView", "Lock view on target", false);
    private final NumberSetting<Double> lockViewStrength = new NumberSetting<>("LockStrength", "Lock view strength", 0.6, 0.1, 1.0);
    private final BooleanSetting aimbot = new BooleanSetting("Aimbot", "Full aimbot mode", false);
    private final BooleanSetting silentAim = new BooleanSetting("SilentAim", "Silent aim (server-side only)", false);
    private final BooleanSetting mouseFilter = new BooleanSetting("MouseFilter", "Filter mouse movements", true);
    private final BooleanSetting respectGCD = new BooleanSetting("RespectGCD", "Respect game's aim mechanics", true);
    private final BooleanSetting centerAim = new BooleanSetting("CenterAim", "Aim at center of hitbox", false);
    private final NumberSetting<Double> hitboxOffset = new NumberSetting<>("HitboxOffset", "Vertical hitbox offset", 0.0, -1.0, 1.0);
    private final BooleanSetting dynamicFOV = new BooleanSetting("DynamicFOV", "Adjust FOV based on distance", false);
    private Entity currentTarget;
    private float targetYaw;
    private float targetPitch;
    private float lastYaw;
    private float lastPitch;
    private long lastAimTime;
    private Vec3d lastTargetPos;
    private boolean isAiming;
    private final List<Entity> potentialTargets = new ArrayList<>();

    public AimAssistModule() {
        super("AimAssist", "Assists with aiming at targets", ModuleCategory.COMBAT);

        addSetting(targetMode);
        addSetting(sortMode);
        addSetting(range);
        addSetting(fov);
        addSetting(targetPlayers);
        addSetting(targetHostile);
        addSetting(targetPassive);
        addSetting(targetInvisible);
        addSetting(targetTeam);
        addSetting(prioritizeAttacking);
        addSetting(aimOnClick);
        addSetting(aimOnTarget);
        addSetting(smoothAim);
        addSetting(horizontalSpeed);
        addSetting(verticalSpeed);
        addSetting(randomizeSpeed);
        addSetting(minAimSpeed);
        addSetting(maxAimSpeed);
        addSetting(aimPrediction);
        addSetting(predictionStrength);
        addSetting(rayTrace);
        addSetting(lockView);
        addSetting(lockViewStrength);
        addSetting(aimbot);
        addSetting(silentAim);
        addSetting(mouseFilter);
        addSetting(respectGCD);
        addSetting(centerAim);
        addSetting(hitboxOffset);
        addSetting(dynamicFOV);

        minAimSpeed.setVisibilityCondition(randomizeSpeed::getValue);
        maxAimSpeed.setVisibilityCondition(randomizeSpeed::getValue);
        predictionStrength.setVisibilityCondition(aimPrediction::getValue);
        lockViewStrength.setVisibilityCondition(lockView::getValue);
        silentAim.setVisibilityCondition(aimbot::getValue);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || mc.world == null) return;

        if(randomizeSpeed.getValue() && System.currentTimeMillis() - lastAimTime > 1000) {
            updateAimSpeeds();
        }

        findTargets();

        if(currentTarget != null) {
            setPrefix(sortMode.getValue() + " " + String.format("%.1f", mc.player.distanceTo(currentTarget)));
        } else {
            setPrefix(sortMode.getValue());
        }
    };

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null || mc.world == null || !event.isPre()) return;

        selectTarget();

        if(!shouldAim()) {
            isAiming = false;
            return;
        }

        if(currentTarget != null) {
            calculateAimAngles();
            if(aimbot.getValue()) {
                if(silentAim.getValue()) {
                    event.setYaw(targetYaw);
                    event.setPitch(targetPitch);
                } else {
                    mc.player.setYaw(targetYaw);
                    mc.player.setPitch(targetPitch);
                }
            } else {
                applyAimAssist();
            }

            isAiming = true;
            lastAimTime = System.currentTimeMillis();
        } else {
            isAiming = false;
        }
    };

    private void findTargets() {
        potentialTargets.clear();

        if(mc.player == null || mc.world == null) return;

        double maxDistance = range.getValue();

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class,
                mc.player.getBoundingBox().expand(maxDistance), this::isValidTarget);

        double fovValue = fov.getValue();
        if(dynamicFOV.getValue()) {
            fovValue = Math.min(fovValue * 2, 360.0);
        }

        for(Entity entity : entities) {
            if(entity.squaredDistanceTo(mc.player) > maxDistance * maxDistance) continue;

            if(fovValue < 360.0 && !isInFOV(entity, fovValue)) continue;

            if(rayTrace.getValue() && !canSeeEntity(entity)) continue;

            potentialTargets.add(entity);
        }

        sortTargets();
    }

    private void sortTargets() {
        if(potentialTargets.isEmpty() || mc.player == null) return;

        switch(sortMode.getValue()) {
            case "Distance" -> potentialTargets.sort(Comparator.comparingDouble(entity ->
                    entity.squaredDistanceTo(mc.player)));
            case "Health" -> potentialTargets.sort(Comparator.comparingDouble(entity -> {
                if(entity instanceof LivingEntity living) {
                    return living.getHealth();
                }
                return Double.MAX_VALUE;
            }));
            case "Angle" -> potentialTargets.sort(Comparator.comparingDouble(this::getAngleToEntity));
            case "Crosshair" -> potentialTargets.sort(Comparator.comparingDouble(entity -> {
                float[] rotations = calculateRotationsToEntity(entity);
                float yawDiff = Math.abs(MathHelper.wrapDegrees(rotations[0] - mc.player.getYaw()));
                float pitchDiff = Math.abs(rotations[1] - mc.player.getPitch());
                return yawDiff + pitchDiff;
            }));
        }

        if(prioritizeAttacking.getValue()) {
            potentialTargets.sort((e1, e2) -> {
                boolean e1Attacking = isEntityAttackingPlayer(e1);
                boolean e2Attacking = isEntityAttackingPlayer(e2);

                if(e1Attacking && !e2Attacking) return -1;
                if(!e1Attacking && e2Attacking) return 1;
                return 0;
            });
        }
    }

    private void selectTarget() {
        if(potentialTargets.isEmpty()) {
            currentTarget = null;
            return;
        }

        currentTarget = potentialTargets.getFirst();

        if(currentTarget != null) {
            if(lastTargetPos == null) {
                lastTargetPos = currentTarget.getPos();
            }
        } else {
            lastTargetPos = null;
        }
    }

    private boolean shouldAim() {
        if(currentTarget == null || mc.player == null) return false;

        if(aimOnClick.getValue() && !mc.options.attackKey.isPressed()) return false;

        if(aimOnTarget.getValue()) {
            float[] rotations = calculateRotationsToEntity(currentTarget);
            float yawDiff = Math.abs(MathHelper.wrapDegrees(rotations[0] - mc.player.getYaw()));
            float pitchDiff = Math.abs(rotations[1] - mc.player.getPitch());

            double aimThreshold = Math.min(60.0, fov.getValue() / 2);
            return !(yawDiff > aimThreshold) && !(pitchDiff > aimThreshold / 2);
        }

        return true;
    }

    private void calculateAimAngles() {
        if(currentTarget == null || mc.player == null) return;

        Vec3d targetPos = getTargetPosition(currentTarget);

        if(aimPrediction.getValue() && lastTargetPos != null) {
            Vec3d velocity = currentTarget.getPos().subtract(lastTargetPos);
            double predictionMultiplier = predictionStrength.getValue();

            double distance = mc.player.distanceTo(currentTarget);
            predictionMultiplier *= Math.min(distance / 5.0, 2.0);

            targetPos = targetPos.add(velocity.multiply(predictionMultiplier));
        }

        lastTargetPos = currentTarget.getPos();

        Vec3d playerPos = mc.player.getEyePos();
        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        targetPitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        if(respectGCD.getValue()) {
            float[] corrected = applyGCDCorrection(targetYaw, targetPitch);
            targetYaw = corrected[0];
            targetPitch = corrected[1];
        }
    }

    private void applyAimAssist() {
        if(mc.player == null) return;

        if(lastYaw == 0 && lastPitch == 0) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }

        float yawDifference = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        float pitchDifference = targetPitch - mc.player.getPitch();

        float smoothFactor = smoothAim.getValue() ?
                horizontalSpeed.getValue().floatValue() : 1.0f;
        float verticalSmoothFactor = smoothAim.getValue() ?
                verticalSpeed.getValue().floatValue() : 1.0f;

        float yawStep = yawDifference / smoothFactor;
        float pitchStep = pitchDifference / verticalSmoothFactor;

        if(lockView.getValue() && isAiming) {
            double lockStrength = lockViewStrength.getValue();
            yawStep *= (float) lockStrength;
            pitchStep *= (float) lockStrength;
        }

        if(mouseFilter.getValue()) {
            double aimProgress = 1.0 - (Math.abs(yawDifference) / (fov.getValue() / 2));
            aimProgress = MathHelper.clamp(aimProgress, 0.0, 1.0);

            double filterStrength = 0.3 + (0.7 * aimProgress);
            yawStep *= (float) filterStrength;
            pitchStep *= (float) filterStrength;
        }

        float newYaw = mc.player.getYaw() + yawStep;
        float newPitch = MathHelper.clamp(mc.player.getPitch() + pitchStep, -90.0F, 90.0F);

        mc.player.setYaw(newYaw);
        mc.player.setPitch(newPitch);

        lastYaw = newYaw;
        lastPitch = newPitch;
    }

    private float[] calculateRotationsToEntity(Entity entity) {
        if(mc.player == null) return new float[]{0, 0};

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(entity);

        double deltaX = targetPos.x - playerPos.x;
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = targetPos.z - playerPos.z;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        return new float[]{yaw, MathHelper.clamp(pitch, -90.0F, 90.0F)};
    }

    private Vec3d getTargetPosition(Entity entity) {
        Box box = entity.getBoundingBox();
        double x = box.minX + (box.maxX - box.minX) * 0.5;
        double z = box.minZ + (box.maxZ - box.minZ) * 0.5;

        double y;
        if(centerAim.getValue()) {
            y = box.minY + (box.maxY - box.minY) * 0.5;
        } else {
            y = box.minY + (box.maxY - box.minY) * 0.85;
        }

        y += hitboxOffset.getValue();

        return new Vec3d(x, y, z);
    }

    private boolean isValidTarget(Entity entity) {
        if(entity == mc.player) return false;
        if(!(entity instanceof LivingEntity living)) return false;
        if(!living.isAlive()) return false;
        if(entity instanceof ArmorStandEntity) return false;

        if(entity.isInvisible() && !targetInvisible.getValue()) return false;

        return switch(entity) {
            case PlayerEntity ignored ->
                    targetPlayers.getValue() || targetMode.getValue().equals("Players") || targetMode.getValue().equals("All");
            case HostileEntity ignored ->
                    targetHostile.getValue() || targetMode.getValue().equals("Hostile") || targetMode.getValue().equals("All");
            case PassiveEntity ignored ->
                    targetPassive.getValue() || targetMode.getValue().equals("Passive") || targetMode.getValue().equals("All");
            default -> false;
        };

    }

    private boolean isInFOV(Entity entity, double fovValue) {
        if(mc.player == null) return false;

        float[] rotations = calculateRotationsToEntity(entity);
        float yawDiff = Math.abs(MathHelper.wrapDegrees(rotations[0] - mc.player.getYaw()));
        float pitchDiff = Math.abs(rotations[1] - mc.player.getPitch());

        return yawDiff <= fovValue && pitchDiff <= fovValue / 2;
    }

    private double getAngleToEntity(Entity entity) {
        if(mc.player == null) return Double.MAX_VALUE;

        float[] rotations = calculateRotationsToEntity(entity);
        float yawDiff = Math.abs(MathHelper.wrapDegrees(rotations[0] - mc.player.getYaw()));
        float pitchDiff = Math.abs(rotations[1] - mc.player.getPitch());

        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }

    private boolean canSeeEntity(Entity entity) {
        if(mc.player == null || mc.world == null) return false;

        if(mc.crosshairTarget instanceof EntityHitResult entityHit && entityHit.getEntity() == entity) {
            return true;
        }

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(entity);

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

    private boolean isEntityAttackingPlayer(Entity entity) {
        if(!(entity instanceof LivingEntity living) || mc.player == null) return false;

        if(living instanceof PlayerEntity) {
            Vec3d lookVec = living.getRotationVec(1.0f);
            Vec3d toPlayer = mc.player.getPos().subtract(living.getPos()).normalize();
            double dot = lookVec.dotProduct(toPlayer);

            return dot > 0.8;
        }

        return living.getAttacking() == mc.player;
    }

    private float[] applyGCDCorrection(float yaw, float pitch) {
        float gcdValue = getGCD();
        float correctedYaw = Math.round(yaw / gcdValue) * gcdValue;
        float correctedPitch = Math.round(pitch / gcdValue) * gcdValue;
        correctedPitch = MathHelper.clamp(correctedPitch, -90.0F, 90.0F);

        return new float[]{correctedYaw, correctedPitch};
    }

    private float getGCD() {
        float sensitivity = mc.options.getMouseSensitivity().getValue().floatValue();
        return sensitivity * 0.6F + 0.2F;
    }

    private void updateAimSpeeds() {

    }

    @Override
    protected void onEnable() {
        lastYaw = 0;
        lastPitch = 0;
        lastAimTime = 0;
        lastTargetPos = null;
        currentTarget = null;
        isAiming = false;

        updateAimSpeeds();
    }

    @Override
    protected void onDisable() {
        currentTarget = null;
        lastTargetPos = null;
        isAiming = false;
    }
}
