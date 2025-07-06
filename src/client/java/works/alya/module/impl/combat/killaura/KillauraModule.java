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

package works.alya.module.impl.combat.killaura;

import works.alya.AlyaClient;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.combat.AttackDelayModule;
import works.alya.utilities.misc.ChatUtility;
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

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class KillauraModule extends Module {

    private final ModeSetting attackMode = new ModeSetting("AttackMode", "Attack mode", "Single", "Single", "Switch", "Multi");
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final NumberSetting<Double> swingDistance = new NumberSetting<>("SwingDistance", "Distance to start swinging", 4.5, 3.0, 6.0);
    private final NumberSetting<Double> reach = new NumberSetting<>("Reach", "Distance to actually attack from", 3.0, 3.0, 6.0);
    private final NumberSetting<Integer> cps = new NumberSetting<>("CPS", "Attacks per second", 12, 1, 20);
    private final BooleanSetting noHitDelay = new BooleanSetting("NoHitDelay", "Remove attack delay", false);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", "Check line of sight to target", true);
    private final BooleanSetting movementCorrection = new BooleanSetting("MoveCorrection", "Do not attack impossibly", true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate to targets", true);
    private final ModeSetting rotationType = new ModeSetting("Rotations", "Rotation mode", "Smooth", "Snap", "Smooth", "Linear");
    private final NumberSetting<Double> rotationSpeed = new NumberSetting<>("RotationSpeed", "Rotation smoothness", 3.0, 1.0, 10.0);
    private final BooleanSetting gcd = new BooleanSetting("GCD", "Greatest Common Divisor stuff idk", true);
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
        super("Killaura", "Kill Aura", "Automatically attack entities", ModuleCategory.COMBAT);

        rotationType.setVisibilityCondition(rotate::getValue);
        rotationSpeed.setVisibilityCondition(() -> rotate.getValue() && rotationType.getValue().equals("Smooth"));
        autoBlockMode.setVisibilityCondition(autoBlock::getValue);
        gcd.setVisibilityCondition(rotate::getValue);

        addSetting(attackMode);
        addSetting(targetMode);
        addSetting(swingDistance);
        addSetting(reach);
        addSetting(cps);
        addSetting(rotate);
        addSetting(rotationType);
        addSetting(rotationSpeed);
        addSetting(autoBlock);
        addSetting(autoBlockMode);
        addSetting(movementCorrection);
        addSetting(gcd);
        addSetting(raycast);
        addSetting(noHitDelay);
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
        if(attackDelayModule.isEnabled() && attackDelayModule.isNewPvpDelay() || noHitDelay.getValue()) {
            cps.setVisibilityCondition(() -> false);
        } else {
            cps.setVisibilityCondition(() -> true);
        }

        setPrefix(rotationType.getValue());
    };

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

            event.setYaw(targetYaw);
            event.setPitch(targetPitch);
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
            case "Single", "Multi" -> currentTarget = targets.isEmpty() ? null : targets.getFirst();
            case "Switch" -> {
                if(switchTimer++ >= 20 && targets.size() > 1) {
                    switchTimer = 0;
                    int currentIndex = targets.indexOf(currentTarget);
                    currentTarget = targets.get((currentIndex + 1) % targets.size());
                } else if(currentTarget == null || !targets.contains(currentTarget)) {
                    currentTarget = targets.isEmpty() ? null : targets.getFirst();
                }
            }
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

        switch(rotationType.getValue()) {
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

        return canHitTarget(currentTarget);
    }

    private boolean canHitTarget(Entity target) {
        if(target == null || mc.player == null) return false;

        Vec3d playerEyes = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(target);
        double distance = playerEyes.distanceTo(targetPos);

        if(distance > reach.getValue()) return false;

        Vec3d direction = targetPos.subtract(playerEyes).normalize();

        float currentYaw = targetYaw;
        float currentPitch = targetPitch;

        Vec3d lookDirection = getLookDirection(currentYaw, currentPitch);

        double dotProduct = direction.dotProduct(lookDirection);
        double angleRadians = Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));
        double angleDegrees = Math.toDegrees(angleRadians);

        double maxAngle = Math.max(5.0, 45.0 - (distance * 8.0));
        maxAngle = Math.min(maxAngle, 45.0);

        if(angleDegrees > maxAngle) return false;

        Vec3d playerVelocity = mc.player.getVelocity();
        double playerSpeed = Math.sqrt(playerVelocity.x * playerVelocity.x + playerVelocity.z * playerVelocity.z);

        if(playerSpeed > 0.3) {
            Vec3d movementDirection = new Vec3d(playerVelocity.x, 0, playerVelocity.z).normalize();
            Vec3d toTarget = new Vec3d(direction.x, 0, direction.z).normalize();

            double movementDot = movementDirection.dotProduct(toTarget);
            double movementAngle = Math.toDegrees(Math.acos(MathHelper.clamp(movementDot, -1.0, 1.0)));

            if(movementAngle > 120.0 && playerSpeed > 0.5) {
                return false;
            }
        }

        return !mc.player.isOnGround() || !(target.getY() > mc.player.getY() + 2.5);
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

        if(currentTarget != null && mc.player.distanceTo(currentTarget) > swingDistance.getValue()) {
            return false;
        }

        if(noHitDelay.getValue()) return true;

        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
        if(attackDelayModule != null && attackDelayModule.isEnabled()) {
            if(attackDelayModule.isNewPvpDelay()) {
                return mc.player.getAttackCooldownProgress(0.0f) >= 1.0f;
            }
        }

        if(cps.isVisible()) {
            long currentTime = System.currentTimeMillis();
            long baseDelay = 1000L / cps.getValue();
            double randomFactor = 0.8 + (random.nextDouble() * 0.4);
            long actualDelay = Math.round(baseDelay * randomFactor);
            actualDelay = Math.max(actualDelay, 50L);

            return currentTime - lastAttackTime >= actualDelay;
        } else {
            return mc.player.getAttackCooldownProgress(0.0f) >= 1.0f;
        }
    }

    private void performAttack() {
        if(mc.player == null || mc.interactionManager == null) return;

        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);

        if(attackDelayModule != null && attackDelayModule.isEnabled()) {
            if(!attackDelayModule.canAttack()) {
                return;
            }
        }

        boolean wasBlocking = isBlocking;
        if(isBlocking) stopBlocking();

        if(attackMode.getValue().equals("Multi")) {
            for(Entity target : targets) {
                double distanceToTarget = mc.player.distanceTo(target);
                if(distanceToTarget <= swingDistance.getValue()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    if(distanceToTarget <= reach.getValue()) {
                        mc.interactionManager.attackEntity(mc.player, target);
                    }
                }
            }
        } else if(currentTarget != null) {
            double distanceToTarget = mc.player.distanceTo(currentTarget);
            if(distanceToTarget <= swingDistance.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
                if(distanceToTarget <= reach.getValue()) {
                    mc.interactionManager.attackEntity(mc.player, currentTarget);
                }
            }
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

        ChatUtility.sendDebug("Distance to target: " + distanceToTarget);
        ChatUtility.sendDebug("Swing distance: " + swingDistance.getValue());
        ChatUtility.sendDebug("Reach distance: " + reach.getValue());

        if(distanceToTarget <= swingDistance.getValue()) {
            ChatUtility.sendDebug("Within swing distance - swinging hand");
            mc.player.swingHand(Hand.MAIN_HAND);

            if(distanceToTarget <= reach.getValue()) {
                ChatUtility.sendDebug("Within reach distance - attacking");
                mc.interactionManager.attackEntity(mc.player, target);
            } else {
                ChatUtility.sendDebug("Not within reach distance - only swinging");
            }
        } else {
            ChatUtility.sendDebug("Not within swing distance - no action");
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

    /**
     * Get the current attack mode for Target Strafe integration
     * @return Current attack mode
     */
    public String getAttackMode() {
        return attackMode.getValue();
    }

    /**
     * Get the current target for Target Strafe integration
     * @return Current target entity
     */
    public Entity getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Check if the module has valid targets
     * @return true if there are valid targets
     */
    public boolean hasTargets() {
        return !targets.isEmpty();
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