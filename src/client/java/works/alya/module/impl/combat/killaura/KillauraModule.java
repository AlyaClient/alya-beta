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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
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
    private final ModeSetting sortMode = new ModeSetting("Sort", "Target sorting", "Distance", "Distance", "Health", "Angle", "Hurt");
    private final NumberSetting<Double> swingDistance = new NumberSetting<>("SwingDistance", "Distance to start swinging", 4.5, 3.0, 8.0);
    private final NumberSetting<Double> reach = new NumberSetting<>("Reach", "Distance to actually attack from", 3.0, 3.0, 6.0);
    private final NumberSetting<Integer> cps = new NumberSetting<>("CPS", "Attacks per second", 12, 1, 20);
    private final NumberSetting<Integer> minCps = new NumberSetting<>("MinCPS", "Minimum attacks per second", 10, 1, 20);
    private final NumberSetting<Integer> maxCps = new NumberSetting<>("MaxCPS", "Maximum attacks per second", 14, 1, 20);
    private final BooleanSetting randomizeCps = new BooleanSetting("RandomCPS", "Randomize CPS", false);
    private final BooleanSetting noHitDelay = new BooleanSetting("NoHitDelay", "Remove attack delay", false);
    private final BooleanSetting raycast = new BooleanSetting("Raycast", "Check line of sight to target", true);
    private final BooleanSetting wallCheck = new BooleanSetting("WallCheck", "Don't attack through walls", true);
    private final BooleanSetting movementCorrection = new BooleanSetting("MoveCorrection", "Do not attack impossibly", true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate to targets", true);
    private final ModeSetting rotationType = new ModeSetting("Rotations", "Rotation mode", "Smooth", "Snap", "Smooth", "Linear", "Predict");
    private final NumberSetting<Double> rotationSpeed = new NumberSetting<>("RotationSpeed", "Rotation smoothness", 3.0, 1.0, 10.0);
    private final NumberSetting<Double> maxRotationAngle = new NumberSetting<>("MaxRotation", "Max rotation per tick", 45.0, 10.0, 180.0);
    private final BooleanSetting gcd = new BooleanSetting("GCD", "Greatest Common Divisor stuff idk", true);
    private final BooleanSetting autoBlock = new BooleanSetting("AutoBlock", "Auto block with sword", false);
    private final ModeSetting autoBlockMode = new ModeSetting("AutoBlockMode", "AutoBlock mode", "Vanilla", "Vanilla", "Packet", "Fake");
    private final NumberSetting<Integer> blockDelay = new NumberSetting<>("BlockDelay", "Block delay in ms", 50, 0, 200);
    private final NumberSetting<Integer> unblockDelay = new NumberSetting<>("UnblockDelay", "Unblock delay in ms", 25, 0, 200);
    private final BooleanSetting smartBlock = new BooleanSetting("SmartBlock", "Smart blocking timing", true);
    private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", "Attack through walls", false);
    private final BooleanSetting predictMovement = new BooleanSetting("Predict", "Predict target movement", false);
    private final BooleanSetting criticals = new BooleanSetting("Criticals", "Try to get critical hits", false);
    private final BooleanSetting keepSprint = new BooleanSetting("KeepSprint", "Keep sprinting while attacking", false);
    private final BooleanSetting ignoreInvisible = new BooleanSetting("IgnoreInvis", "Ignore invisible entities", true);
    private final BooleanSetting prioritizeTarget = new BooleanSetting("PrioritizeTarget", "Prioritize certain targets", false);
    private final NumberSetting<Double> fov = new NumberSetting<>("FOV", "Field of view", 360.0, 30.0, 360.0);
    private final NumberSetting<Integer> switchDelay = new NumberSetting<>("SwitchDelay", "Switch target delay", 200, 50, 1000);

    private final List<Entity> targets = new ArrayList<>();
    private Entity currentTarget;
    private Entity lastTarget;
    private int switchTimer = 0;
    private boolean isBlocking = false;
    private long lastAttackTime = 0;
    private long lastBlockTime = 0;
    private long lastUnblockTime = 0;
    private long lastSwitchTime = 0;
    private final Random random = new Random();
    private float lastYaw = 0f;
    private float lastPitch = 0f;
    private float targetYaw = 0f;
    private float targetPitch = 0f;
    private long lastRotationTime = 0;
    private Vec3d lastTargetPos = null;
    private int attackCount = 0;
    private boolean wasBlocking = false;
    private int currentCps = 12;

    public KillauraModule() {
        super("Killaura", "Kill Aura", "Automatically attack entities", ModuleCategory.COMBAT);

        rotationType.setVisibilityCondition(rotate::getValue);
        rotationSpeed.setVisibilityCondition(() -> rotate.getValue() && (rotationType.getValue().equals("Smooth") || rotationType.getValue().equals("Predict")));
        maxRotationAngle.setVisibilityCondition(() -> rotate.getValue() && !rotationType.getValue().equals("Snap"));
        autoBlockMode.setVisibilityCondition(autoBlock::getValue);
        blockDelay.setVisibilityCondition(() -> autoBlock.getValue() && !autoBlockMode.getValue().equals("Fake"));
        unblockDelay.setVisibilityCondition(() -> autoBlock.getValue() && !autoBlockMode.getValue().equals("Fake"));
        smartBlock.setVisibilityCondition(autoBlock::getValue);
        gcd.setVisibilityCondition(rotate::getValue);
        minCps.setVisibilityCondition(randomizeCps::getValue);
        maxCps.setVisibilityCondition(randomizeCps::getValue);
        switchDelay.setVisibilityCondition(() -> attackMode.getValue().equals("Switch"));
        wallCheck.setVisibilityCondition(() -> !throughWalls.getValue());

        addSetting(attackMode);
        addSetting(targetMode);
        addSetting(sortMode);
        addSetting(swingDistance);
        addSetting(reach);
        addSetting(cps);
        addSetting(randomizeCps);
        addSetting(minCps);
        addSetting(maxCps);
        addSetting(rotate);
        addSetting(rotationType);
        addSetting(rotationSpeed);
        addSetting(maxRotationAngle);
        addSetting(autoBlock);
        addSetting(autoBlockMode);
        addSetting(blockDelay);
        addSetting(unblockDelay);
        addSetting(smartBlock);
        addSetting(movementCorrection);
        addSetting(gcd);
        addSetting(raycast);
        addSetting(wallCheck);
        addSetting(throughWalls);
        addSetting(predictMovement);
        addSetting(criticals);
        addSetting(keepSprint);
        addSetting(ignoreInvisible);
        addSetting(prioritizeTarget);
        addSetting(fov);
        addSetting(switchDelay);
        addSetting(noHitDelay);
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
        if(attackDelayModule != null && attackDelayModule.isEnabled() && attackDelayModule.isNewPvpDelay() || noHitDelay.getValue()) {
            cps.setVisibilityCondition(() -> false);
        } else {
            cps.setVisibilityCondition(() -> true);
        }

        if(randomizeCps.getValue()) {
            if(System.currentTimeMillis() - lastAttackTime > 1000) {
                currentCps = random.nextInt(maxCps.getValue() - minCps.getValue() + 1) + minCps.getValue();
            }
        } else {
            currentCps = cps.getValue();
        }

        setPrefix(rotationType.getValue() + " " + targets.size());
    };

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || mc.world == null || !event.isPre()) return;

        findTargets();
        selectTarget();

        if(targets.isEmpty()) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(rotate.getValue() && currentTarget != null) {
            calculateRotations();
            event.setYaw(targetYaw);
            event.setPitch(targetPitch);
        }

        handleAutoBlock();

        if(canAttack() && isValidHit()) {
            performAttack();
        }
    };

    private void findTargets() {
        targets.clear();

        if(mc.player == null || mc.world == null) return;

        double maxDistance = Math.max(swingDistance.getValue(), reach.getValue()) + 1.0;
        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class,
                mc.player.getBoundingBox().expand(maxDistance), this::isValidTarget);

        for(Entity entity : entities) {
            if(entity.squaredDistanceTo(mc.player) > maxDistance * maxDistance) continue;
            if(fov.getValue() < 360.0 && !isInFOV(entity)) continue;
            if(raycast.getValue() && wallCheck.getValue() && !canSeeTarget(entity)) continue;
            if(ignoreInvisible.getValue() && entity.isInvisible()) continue;

            targets.add(entity);
        }

        sortTargets();
    }

    private void sortTargets() {
        if(mc.player == null) return;

        switch(sortMode.getValue()) {
            case "Distance" -> targets.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));
            case "Health" -> targets.sort(Comparator.comparingDouble(entity -> {
                if(entity instanceof LivingEntity living) {
                    return living.getHealth();
                }
                return 0.0;
            }));
            case "Angle" -> targets.sort(Comparator.comparingDouble(this::getAngleToEntity));
            case "Hurt" -> targets.sort(Comparator.comparingInt(entity -> {
                if(entity instanceof LivingEntity living) {
                    return living.hurtTime;
                }
                return 0;
            }));
        }
    }

    private boolean isInFOV(Entity entity) {
        if(mc.player == null) return false;

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d entityPos = entity.getBoundingBox().getCenter();
        Vec3d direction = entityPos.subtract(playerPos).normalize();

        Vec3d lookDirection = getLookDirection(mc.player.getYaw(), mc.player.getPitch());

        double dotProduct = direction.dotProduct(lookDirection);
        double angle = Math.toDegrees(Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0)));

        return angle <= fov.getValue() / 2.0;
    }

    private double getAngleToEntity(Entity entity) {
        if(mc.player == null) return 0.0;

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d entityPos = entity.getBoundingBox().getCenter();
        Vec3d direction = entityPos.subtract(playerPos).normalize();

        Vec3d lookDirection = getLookDirection(mc.player.getYaw(), mc.player.getPitch());

        double dotProduct = direction.dotProduct(lookDirection);
        return Math.acos(MathHelper.clamp(dotProduct, -1.0, 1.0));
    }

    private boolean canSeeTarget(Entity target) {
        if(mc.player == null || mc.world == null) return false;
        if(throughWalls.getValue()) return true;

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

        if(entity instanceof ArmorStandEntity) return false;

        return switch(targetMode.getValue()) {
            case "Players" -> entity instanceof PlayerEntity;
            case "Passive" -> entity instanceof PassiveEntity;
            case "Hostile" -> entity instanceof HostileEntity;
            case "All" -> entity instanceof PlayerEntity || entity instanceof PassiveEntity || entity instanceof HostileEntity;
            default -> false;
        };
    }

    private void selectTarget() {
        long currentTime = System.currentTimeMillis();

        switch(attackMode.getValue()) {
            case "Single", "Multi" -> {
                if(prioritizeTarget.getValue() && currentTarget != null && targets.contains(currentTarget)) {
                    return;
                }
                currentTarget = targets.isEmpty() ? null : targets.getFirst();
            }
            case "Switch" -> {
                if(currentTime - lastSwitchTime >= switchDelay.getValue()) {
                    if(targets.size() > 1) {
                        int currentIndex = targets.indexOf(currentTarget);
                        currentTarget = targets.get((currentIndex + 1) % targets.size());
                        lastSwitchTime = currentTime;
                    } else if(currentTarget == null || !targets.contains(currentTarget)) {
                        currentTarget = targets.isEmpty() ? null : targets.getFirst();
                    }
                } else if(currentTarget == null || !targets.contains(currentTarget)) {
                    currentTarget = targets.isEmpty() ? null : targets.getFirst();
                }
            }
        }

        if(currentTarget != lastTarget) {
            lastTarget = currentTarget;
            lastTargetPos = currentTarget != null ? currentTarget.getPos() : null;
        }
    }

    private float[] applyGCDCorrection(float yaw, float pitch) {
        if(!gcd.getValue()) {
            return new float[]{yaw, pitch};
        }

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

    private void calculateRotations() {
        if(currentTarget == null || mc.player == null) return;

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = getTargetPosition(currentTarget);

        if(predictMovement.getValue()) {
            targetPos = getPredictedPosition(currentTarget);
        }

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

                float maxAngle = maxRotationAngle.getValue().floatValue();
                yawDelta = MathHelper.clamp(yawDelta, -maxAngle, maxAngle);
                pitchDelta = MathHelper.clamp(pitchDelta, -maxAngle, maxAngle);

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
            case "Predict" -> {
                Vec3d predictedPos = getPredictedTargetPosition(currentTarget);

                double predDeltaX = predictedPos.x - playerPos.x;
                double predDeltaY = predictedPos.y - playerPos.y;
                double predDeltaZ = predictedPos.z - playerPos.z;

                double predHorizontalDistance = Math.sqrt(predDeltaX * predDeltaX + predDeltaZ * predDeltaZ);

                float predictedYaw = (float) Math.toDegrees(Math.atan2(predDeltaZ, predDeltaX)) - 90.0F;
                float predictedPitch = (float) -Math.toDegrees(Math.atan2(predDeltaY, predHorizontalDistance));
                predictedPitch = MathHelper.clamp(predictedPitch, -90.0F, 90.0F);

                float smoothness = rotationSpeed.getValue().floatValue();
                float yawDelta = MathHelper.wrapDegrees(predictedYaw - lastYaw);
                float pitchDelta = predictedPitch - lastPitch;

                float accelerationFactor = calculateAccelerationFactor(currentTarget);
                smoothness *= accelerationFactor;

                float maxAngle = maxRotationAngle.getValue().floatValue();
                yawDelta = MathHelper.clamp(yawDelta, -maxAngle, maxAngle);
                pitchDelta = MathHelper.clamp(pitchDelta, -maxAngle, maxAngle);

                lastYaw += yawDelta / smoothness;
                lastPitch += pitchDelta / smoothness;
                lastPitch = MathHelper.clamp(lastPitch, -90.0F, 90.0F);
            }
        }

        float[] correctedRotations = applyGCDCorrection(lastYaw, lastPitch);
        targetYaw = correctedRotations[0];
        targetPitch = correctedRotations[1];
    }

    private Vec3d getPredictedTargetPosition(Entity target) {
        if(mc.player == null) return target.getBoundingBox().getCenter();
        if(!(target instanceof LivingEntity living)) {
            return getTargetPosition(target);
        }

        Vec3d currentPos = target.getPos();
        Vec3d velocity = target.getVelocity();

        double distance = mc.player.distanceTo(target);

        double targetSpeed = velocity.length();
        double predictionTime = Math.min(distance / 10.0, 1.0);

        if(targetSpeed > 0.1) {
            predictionTime *= (1.0 + targetSpeed * 2.0);
        }

        predictionTime += (random.nextGaussian() * 0.1);
        predictionTime = Math.max(0.1, Math.min(predictionTime, 1.5));

        Vec3d predictedVelocity = velocity;
        if(!target.isOnGround()) {
            predictedVelocity = velocity.add(0, -0.08 * predictionTime, 0);
        }

        if(target instanceof PlayerEntity player) {
            Vec3d lookDir = player.getRotationVector();
            double alignment = velocity.normalize().dotProduct(lookDir);
            if(alignment < 0.5) {
                predictionTime *= 0.7;
            }
        }

        Vec3d predictedPos = currentPos.add(predictedVelocity.multiply(predictionTime));

        Box targetBox = target.getBoundingBox();
        Vec3d offset = new Vec3d(
                targetBox.getLengthX() * 0.5,
                targetBox.getLengthY() * 0.3,
                targetBox.getLengthZ() * 0.5
        );

        return predictedPos.add(offset);
    }

    private float calculateAccelerationFactor(Entity target) {
        if(mc.player == null) return 1.0f;
        if(target == null) return 1.0f;

        Vec3d velocity = target.getVelocity();
        double speed = velocity.length();

        float factor = 1.0f;

        if(speed > 0.2) {
            factor *= (float)(0.7 + speed * 1.5);
        }

        double distance = mc.player.distanceTo(target);
        if(distance < 3.0) {
            factor *= (float)(0.8 + (3.0 - distance) * 0.3);
        }

        if(lastTargetPos != null) {
            Vec3d currentPos = target.getPos();
            Vec3d currentMovement = currentPos.subtract(lastTargetPos);

            if(target instanceof LivingEntity living) {
                float yawChange = Math.abs(living.getYaw() - living.bodyYaw);
                if(yawChange > 5.0f) {
                    factor *= 1.3f;
                }
            }
        }

        return Math.max(0.5f, Math.min(factor, 3.0f));
    }

    private Vec3d getPredictedPosition(Entity entity) {
        if(mc.player == null) return entity.getBoundingBox().getCenter();
        if(lastTargetPos == null) return entity.getBoundingBox().getCenter();

        Vec3d currentPos = entity.getPos();
        Vec3d velocity = currentPos.subtract(lastTargetPos);

        lastTargetPos = currentPos;

        double distance = mc.player.distanceTo(entity);
        double predictionMultiplier = Math.min(distance / 5.0, 2.0);

        return entity.getBoundingBox().getCenter().add(velocity.multiply(predictionMultiplier));
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
        Vec3d lookDirection = getLookDirection(targetYaw, targetPitch);
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
        Box box = target.getBoundingBox();
        return new Vec3d(
                box.minX + (box.maxX - box.minX) * 0.5,
                box.minY + (box.maxY - box.minY) * 0.3,
                box.minZ + (box.maxZ - box.minZ) * 0.5
        );
    }

    private boolean canAttack() {
        if(targets.isEmpty() || mc.player == null) return false;

        if(currentTarget != null && mc.player.distanceTo(currentTarget) > swingDistance.getValue()) {
            return false;
        }

        if(criticals.getValue() && mc.player.isOnGround() && !mc.player.isInLava() && !mc.player.isSubmergedInWater()) {
            return false;
        }

        if(noHitDelay.getValue()) return true;

        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
        if(attackDelayModule != null && attackDelayModule.isEnabled()) {
            if(attackDelayModule.isNewPvpDelay()) {
                return mc.player.getAttackCooldownProgress(0.0f) >= 0.9f;
            }
        }

        if(cps.isVisible()) {
            long currentTime = System.currentTimeMillis();
            long baseDelay = 1000L / currentCps;
            double randomFactor = 0.8 + (random.nextDouble() * 0.4);
            long actualDelay = Math.round(baseDelay * randomFactor);
            actualDelay = Math.max(actualDelay, 25L);

            return currentTime - lastAttackTime >= actualDelay;
        } else {
            return mc.player.getAttackCooldownProgress(0.0f) >= 0.9f;
        }
    }

    private void performAttack() {
        if(mc.player == null || mc.interactionManager == null) return;

        AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
        if(attackDelayModule != null && attackDelayModule.isEnabled() && !attackDelayModule.canAttack()) {
            return;
        }

        wasBlocking = isBlocking;
        if(isBlocking && smartBlock.getValue()) {
            unblockForAttack();
        }

        if(criticals.getValue() && mc.player.isOnGround()) {
            mc.player.jump();
        }

        if(attackMode.getValue().equals("Multi")) {
            for(Entity target : targets) {
                if(target == null) continue;
                double distanceToTarget = mc.player.distanceTo(target);
                if(distanceToTarget <= swingDistance.getValue()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                    if(distanceToTarget <= reach.getValue()) {
                        if(!keepSprint.getValue()) {
                            mc.player.setSprinting(false);
                        }
                        mc.interactionManager.attackEntity(mc.player, target);
                    }
                }
            }
        } else if(currentTarget != null) {
            double distanceToTarget = mc.player.distanceTo(currentTarget);
            if(distanceToTarget <= swingDistance.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
                if(distanceToTarget <= reach.getValue()) {
                    if(!keepSprint.getValue()) {
                        mc.player.setSprinting(false);
                    }
                    mc.interactionManager.attackEntity(mc.player, currentTarget);
                }
            }
        }

        lastAttackTime = System.currentTimeMillis();
        attackCount++;

        if(wasBlocking && autoBlock.getValue() && smartBlock.getValue()) {
            scheduleReblock();
        }
    }

    private void handleAutoBlock() {
        if(mc.player == null || !autoBlock.getValue()) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(targets.isEmpty() || !canBlock()) {
            if(isBlocking) stopBlocking();
            return;
        }

        if(smartBlock.getValue()) {
            boolean shouldBlock = shouldAutoBlock();
            if(shouldBlock && !isBlocking) {
                startBlocking();
            } else if(!shouldBlock && isBlocking) {
                stopBlocking();
            }
        } else {
            if(!isBlocking) {
                startBlocking();
            }
        }
    }

    private boolean shouldAutoBlock() {
        if(mc.player == null || currentTarget == null) return false;

        float cooldown = mc.player.getAttackCooldownProgress(0.0f);
        if(cooldown < 0.8f) return true;

        double distance = mc.player.distanceTo(currentTarget);
        if(distance > reach.getValue() + 1.0) return true;

        if(currentTarget instanceof LivingEntity living) {
            return living.hurtTime <= 0;
        }

        return false;
    }

    private void unblockForAttack() {
        if(!isBlocking) return;

        long currentTime = System.currentTimeMillis();
        if(currentTime - lastUnblockTime < unblockDelay.getValue()) return;

        stopBlocking();
        lastUnblockTime = currentTime;
    }

    private void scheduleReblock() {
        new Thread(() -> {
            try {
                Thread.sleep(blockDelay.getValue());
                if(isEnabled() && autoBlock.getValue() && !isBlocking) {
                    startBlocking();
                }
            } catch(InterruptedException ignored) {
            }
        }).start();
    }

    private boolean canBlock() {
        if(mc.player == null) return false;
        ItemStack mainHand = mc.player.getMainHandStack();
        if(mainHand.isEmpty()) return false;

        return mainHand.getItem() instanceof AxeItem || mainHand.getItem().getName().toString().toLowerCase().contains("sword");
    }

    private void startBlocking() {
        if(isBlocking || mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        if(currentTime - lastBlockTime < blockDelay.getValue()) return;

        switch(autoBlockMode.getValue()) {
            case "Vanilla" -> mc.options.useKey.setPressed(true);
            case "Packet" -> {
                ItemStack mainHand = mc.player.getMainHandStack();
                if(!mainHand.isEmpty()) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(
                            Hand.MAIN_HAND,
                            0,
                            mc.player.getYaw(),
                            mc.player.getPitch()
                    ));
                }
            }
            case "Fake" -> {
            }
        }

        isBlocking = true;
        lastBlockTime = currentTime;
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
            case "Fake" -> {
            }
        }

        isBlocking = false;
    }

    public String getAttackMode() {
        return attackMode.getValue();
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public boolean hasTargets() {
        return !targets.isEmpty();
    }

    public List<Entity> getTargets() {
        return new ArrayList<>(targets);
    }

    public int getAttackCount() {
        return attackCount;
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }

        lastAttackTime = 0;
        lastBlockTime = 0;
        lastUnblockTime = 0;
        lastSwitchTime = 0;
        switchTimer = 0;
        isBlocking = false;
        attackCount = 0;
        wasBlocking = false;
        lastTargetPos = null;
        currentCps = cps.getValue();
    }

    @Override
    protected void onDisable() {
        if(isBlocking) {
            stopBlocking();
        }

        targets.clear();
        currentTarget = null;
        lastTarget = null;
        switchTimer = 0;
        isBlocking = false;
        attackCount = 0;
        wasBlocking = false;
        lastTargetPos = null;
    }
}