package dev.thoq.module.impl.combat.killaura;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.longjump.verus.VerusLongJump;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class KillauraModule extends Module {
    ModeSetting type = new ModeSetting("Type", "Type of Killaura to use", "Switch", "Single", "Switch", "Multi");
    ModeSetting target = new ModeSetting("Target", "Target of Killaura to use", "Players", "Players", "Passive", "Hostile");
    BooleanSetting autoBlock = new BooleanSetting("AutoBlock", "Automatically blocks when attacking", true);
    ModeSetting autoBlockType = new ModeSetting("AutoBlockType", "Type of AutoBlock to use", "Fake", "Vanilla", "Fake");
    BooleanSetting nonLinear = new BooleanSetting("NonLinear", "Makes rotation more human-like with jitter", true);
    NumberSetting<Integer> attacksPerSecond = new NumberSetting<>("AttacksPerSecond", "How often to attack", 12, 1, 20);
    BooleanSetting serverSideRotations = new BooleanSetting("ServerSideRotations", "Rotations only sent to server, do not affect the player's viewport", true);

    private final List<Entity> targets = new ArrayList<>();
    private Entity currentTarget;
    private int switchTimer = 0;
    private boolean blocking = false;
    private final Random random = new Random();
    private long lastRotationTime = 0;
    private long lastAttackTime = 0;
    private float clientYaw;
    private float clientPitch;
    private float serverYaw;
    private float serverPitch;
    private Entity pendingAttackTarget = null;
    private boolean rotationsQueued = false;
    private int rotationConfirmTicks = 0;

    public KillauraModule() {
        super("Killaura", "Automatically attack entities", ModuleCategory.COMBAT);

        autoBlockType.setVisibilityCondition(() -> autoBlock.getValue());

        addSetting(type);
        addSetting(target);
        addSetting(attacksPerSecond);
        addSetting(autoBlock);
        addSetting(autoBlockType);
        addSetting(nonLinear);
        addSetting(serverSideRotations);
    }

    @Override
    protected void onPreTick() {
        if(mc.player == null || mc.world == null) return;

        clientYaw = mc.player.getYaw();
        clientPitch = mc.player.getPitch();

        findTargets();

        if(targets.isEmpty()) {
            currentTarget = null;
            rotationsQueued = false;
            pendingAttackTarget = null;
            return;
        }

        switch(type.getValue()) {
            case "Single" -> {
                currentTarget = targets.getFirst();
                queueRotationsAndAttack(currentTarget);
            }
            case "Switch" -> {
                if(switchTimer++ >= 10) {
                    switchTimer = 0;
                    currentTarget = targets.get(targets.size() > 1 ? 1 : 0);
                } else if(currentTarget == null || !targets.contains(currentTarget)) {
                    currentTarget = targets.getFirst();
                }
                queueRotationsAndAttack(currentTarget);
            }
            case "Multi" -> {
                if(!targets.isEmpty()) queueRotationsAndAttack(targets.getFirst());
            }
        }

        if(rotationsQueued) rotateToTarget();
    }

    @Override
    protected void onTick() {
        if(mc.player == null || mc.world == null) return;

        handleAutoBlock();

        if(rotationsQueued && pendingAttackTarget != null) {
            rotationConfirmTicks++;

            if(rotationConfirmTicks >= 1 && canAttack()) {
                attackTarget(pendingAttackTarget);
                if(type.getValue().equals("Multi") && targets.size() > 1) {
                    for(int i = 1; i < Math.min(targets.size(), 3); i++) {
                        attackTarget(targets.get(i));
                    }
                }
                pendingAttackTarget = null;
                rotationConfirmTicks = 0;
            }
        }
    }

    @Override
    protected void onPostTick() {
        if(mc.player == null || mc.world == null) return;

        if(serverSideRotations.getValue()) {
            mc.player.setYaw(clientYaw);
            mc.player.setPitch(clientPitch);
        }
    }

    private void queueRotationsAndAttack(Entity target) {
        if(target == null || mc.player == null) return;

        pendingAttackTarget = target;
        rotationsQueued = true;

        Vec3d playerPos = mc.player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();

        double deltaX = targetPos.x - playerPos.x;
        double deltaY = (targetPos.y - playerPos.y);
        double deltaZ = targetPos.z - playerPos.z;

        deltaY -= 0.2;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));

        if(nonLinear.getValue()) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastRotationTime > 100) {
                lastRotationTime = currentTime;

                float jitterAmount = 1.5f;
                float yawJitter = (random.nextFloat() - 0.5f) * jitterAmount;
                float pitchJitter = (random.nextFloat() - 0.5f) * jitterAmount * 0.75f;

                yaw += yawJitter;
                pitch = MathHelper.clamp(pitch + pitchJitter, -90.0F, 90.0F);
            }
        }

        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        serverYaw = yaw;
        serverPitch = pitch;
    }

    private void rotateToTarget() {
        if(mc.player == null) return;

        mc.player.setYaw(serverYaw);
        mc.player.setPitch(serverPitch);
    }

    private boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        int delay = 1000 / attacksPerSecond.getValue();

        if(nonLinear.getValue()) {
            delay += (random.nextInt(30) - 15);
        }

        if(currentTime - lastAttackTime >= delay) {
            lastAttackTime = currentTime;
            return true;
        }
        return false;
    }

    private void findTargets() {
        if(mc.player == null || mc.world == null) return;

        targets.clear();

        double range = 4.5;
        Box box = new Box(
                mc.player.getX() - range, mc.player.getY() - range, mc.player.getZ() - range,
                mc.player.getX() + range, mc.player.getY() + range, mc.player.getZ() + range
        );

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class, box, entity -> {
            if(!(entity instanceof LivingEntity) || entity == mc.player) return false;
            return switch(entity) {
                case PlayerEntity ignored when target.getValue().equals("Players") -> true;
                case PassiveEntity ignored when target.getValue().equals("Passive") -> true;
                case HostileEntity ignored when target.getValue().equals("Hostile") -> true;
                default -> false;
            };
        });

        entities.sort(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(mc.player)));
        targets.addAll(entities);
    }

    private void attackTarget(Entity target) {
        if(mc.player == null || mc.interactionManager == null || target == null) return;

        if(autoBlock.getValue() && blocking) stopBlocking();

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void handleAutoBlock() {
        if(mc.player == null) return;

        if(!autoBlock.getValue() || currentTarget == null) {
            if(blocking) {
                stopBlocking();
            }
            return;
        }

        if(mc.player.getAttackCooldownProgress(0) >= 1.0f) {
            startBlocking();
        } else {
            stopBlocking();
        }
    }

    private void startBlocking() {
        if(mc.player == null) return;

        ItemStack mainHandStack = mc.player.getMainHandStack();

        if(!blocking && canBlock(mainHandStack)) {
            blocking = true;
            if(autoBlockType.getValue().equals("Vanilla")) {
                mc.options.useKey.setPressed(true);
            }
        }
    }

    private boolean canBlock(ItemStack stack) {
        if(stack.isEmpty()) return false;

        Item item = stack.getItem();
        String itemName = item.toString().toLowerCase();

        return itemName.contains("sword");
    }

    private void stopBlocking() {
        if(blocking) {
            blocking = false;
            if(autoBlockType.getValue().equals("Vanilla")) {
                mc.options.useKey.setPressed(false);
            }
        }
    }

    @Override
    protected void onEnable() {
        VerusLongJump.reset();
        lastAttackTime = 0;
        rotationsQueued = false;
        pendingAttackTarget = null;
    }

    @Override
    protected void onDisable() {
        if(blocking) {
            stopBlocking();
        }

        if(mc.player != null && serverSideRotations.getValue()) {
            mc.player.setYaw(clientYaw);
            mc.player.setPitch(clientPitch);
        }

        currentTarget = null;
        targets.clear();
        rotationsQueued = false;
        pendingAttackTarget = null;
    }
}
