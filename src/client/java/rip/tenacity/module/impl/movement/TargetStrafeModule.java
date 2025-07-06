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

package rip.tenacity.module.impl.movement;

import rip.tenacity.TenacityClient;
import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.config.setting.impl.ModeSetting;
import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.combat.killaura.KillauraModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class TargetStrafeModule extends Module {
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final NumberSetting<Float> range = new NumberSetting<>("Range", "Distance from target to strafe", 3.0f, 0.1f, 6.0f);
    private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Strafe speed", 0.28f, 0.1f, 1.0f);
    private final BooleanSetting adaptiveSpeed = new BooleanSetting("Adaptive Speed", "Adjust speed based on distance", true);
    private final BooleanSetting jumpStrafe = new BooleanSetting("Jump Strafe", "Allow strafing in air", false);
    private final BooleanSetting onlyWhenKillaura = new BooleanSetting("Only with Killaura", "Only strafe when Killaura is enabled", true);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", "Jump while strafing", false);
    private final NumberSetting<Float> jumpChance = new NumberSetting<>("Jump Chance", "Chance to jump while strafing", 0.1f, 0.01f, 1.0f);

    private Entity currentTarget = null;
    private float strafeDirection = 1.0f;
    private int directionChangeTimer = 0;
    private int jumpTimer = 0;

    public TargetStrafeModule() {
        super("TargetStrafeModule", "Target Strafe", "Automatically move around targets", ModuleCategory.MOVEMENT);

        jumpChance.setVisibilityCondition(autoJump::getValue);

        addSetting(targetMode);
        addSetting(range);
        addSetting(speed);
        addSetting(adaptiveSpeed);
        addSetting(jumpStrafe);
        addSetting(onlyWhenKillaura);
        addSetting(autoJump);
        addSetting(jumpChance);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if (mc.player == null || mc.world == null || !event.isPre()) return;

        if (onlyWhenKillaura.getValue()) {
            KillauraModule killaura = TenacityClient.INSTANCE.getModuleRepository().getModule(KillauraModule.class);
            if (killaura == null || !killaura.isEnabled()) {
                currentTarget = null;
                return;
            }

            String attackMode = killaura.getAttackMode();
            if (attackMode.equals("Single") || attackMode.equals("Switch")) {
                Entity killauraTarget = killaura.getCurrentTarget();
                
                if (killauraTarget != null) {
                    currentTarget = killauraTarget;
                } else if (currentTarget != null && isValidStrafeTarget(currentTarget)) {
                } else {
                    currentTarget = null;
                }
            } else {
                currentTarget = null;
                return;
            }
        } else {
            findTarget();
        }

        if (currentTarget == null) return;

        if (!shouldStrafe()) return;

        performStrafe();

        if (autoJump.getValue() && shouldJump()) {
            mc.player.jump();
        }
    };

    private boolean isValidStrafeTarget(Entity entity) {
        if (mc.player == null || entity == null || !entity.isAlive()) return false;
        
        double distance = mc.player.distanceTo(entity);
        if (distance > range.getValue() * 1.5) return false;
        
        return isValidTarget(entity);
    }

    private void findTarget() {
        if (mc.player == null || mc.world == null) return;

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class,
                mc.player.getBoundingBox().expand(range.getValue()),
                this::isValidTarget);

        if (entities.isEmpty()) {
            currentTarget = null;
            return;
        }

        entities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));
        currentTarget = entities.getFirst();
    }

    private boolean isValidTarget(Entity entity) {
        if (mc.player == null) return false;
        if (!(entity instanceof LivingEntity) || entity == mc.player || !entity.isAlive()) {
            return false;
        }

        double distance = mc.player.distanceTo(entity);
        if (distance > range.getValue()) return false;

        return switch (targetMode.getValue()) {
            case "Players" -> entity instanceof PlayerEntity;
            case "Passive" -> entity instanceof PassiveEntity;
            case "Hostile" -> entity instanceof HostileEntity;
            case "All" -> entity instanceof PlayerEntity || entity instanceof PassiveEntity || entity instanceof HostileEntity;
            default -> false;
        };
    }

    private boolean shouldStrafe() {
        if (mc.player == null || currentTarget == null) return false;

        if (!currentTarget.isAlive()) {
            currentTarget = null;
            return false;
        }

        double distance = mc.player.distanceTo(currentTarget);

        if (distance > range.getValue() * 1.2) {
            currentTarget = null;
            return false;
        }

        return mc.player.isOnGround() || jumpStrafe.getValue();
    }

    private void performStrafe() {
        if (mc.player == null || currentTarget == null) return;

        Vec3d playerPos = mc.player.getPos();
        Vec3d targetPos = currentTarget.getPos();

        double deltaX = targetPos.x - playerPos.x;
        double deltaZ = targetPos.z - playerPos.z;

        float angleToTarget = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;

        float strafeAngle = angleToTarget + (90.0f * strafeDirection);

        if (directionChangeTimer++ >= 60) {
            strafeDirection = -strafeDirection;
            directionChangeTimer = 0;
        }

        float currentSpeed = speed.getValue();

        if (adaptiveSpeed.getValue()) {
            double distance = mc.player.distanceTo(currentTarget);
            float targetDistance = range.getValue() * 0.7f;
            float distanceRatio = (float) (distance / targetDistance);

            if (distanceRatio < 0.8f) {
                currentSpeed *= 1.2f;
            } else if (distanceRatio > 1.2f) {
                currentSpeed *= 0.8f;
            }
        }

        double radians = Math.toRadians(strafeAngle);
        double xMotion = -Math.sin(radians) * currentSpeed;
        double zMotion = Math.cos(radians) * currentSpeed;

        Vec3d currentVelocity = mc.player.getVelocity();
        mc.player.setVelocity(xMotion, currentVelocity.y, zMotion);
    }

    private boolean shouldJump() {
        if (mc.player == null) return false;

        if (!mc.player.isOnGround()) return false;

        if (jumpTimer > 0) {
            jumpTimer--;
            return false;
        }

        if (Math.random() > jumpChance.getValue()) return false;

        jumpTimer = 10;
        return true;
    }

    @Override
    protected void onEnable() {
        currentTarget = null;
        directionChangeTimer = 0;
        jumpTimer = 0;
        strafeDirection = 1.0f;
    }

    @Override
    protected void onDisable() {
        currentTarget = null;
        directionChangeTimer = 0;
        jumpTimer = 0;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public boolean isStrafing() {
        return isEnabled() && currentTarget != null;
    }
}