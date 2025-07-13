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
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.ReachEvent;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.misc.RaycastUtility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class ReachModule extends Module {

    /**
     * @credit: NezhaAnticheat - for funny name
     */
    private static final BooleanSetting remoteSpleefHacks = new BooleanSetting("RemoteSpleefHacks", "Allows you to hit blocks from very far away", false);

    /**
     * Settings for controlling player reach distance
     */
    private static final NumberSetting<Double> reachDistance = new NumberSetting<>("Reach", "Controls the player's reach distance", 6.0, 3.0, 100.0);
    private static final BooleanSetting noLimitReach = new BooleanSetting("NoLimitReach", "Removes the maximum reach limit completely", false);
    private static final BooleanSetting attackThroughWalls = new BooleanSetting("AttackThroughWalls", "Attack entities through walls", false);
    private static final BooleanSetting autoAttack = new BooleanSetting("AutoAttack", "Automatically attack entities in range", false);
    private static final NumberSetting<Double> autoAttackRange = new NumberSetting<>("AutoAttackRange", "Range for auto attack", 4.0, 3.0, 6.0);

    public ReachModule() {
        super("Reach", "billy big-arms", ModuleCategory.COMBAT);

        addSetting(reachDistance);
        addSetting(noLimitReach);
        addSetting(attackThroughWalls);
        addSetting(autoAttack);
        addSetting(autoAttackRange);
        addSetting(remoteSpleefHacks);

        autoAttackRange.setVisibilityCondition(autoAttack::getValue);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || mc.world == null || mc.getNetworkHandler() == null || !event.isPre()) return;
        double lastReach = 0d;
        if(remoteSpleefHacks.getValue() && mc.options.attackKey.isPressed()) {
            BlockHitResult hitResult = RaycastUtility.raycast(mc, noLimitReach.getValue() ? Double.MAX_VALUE : reachDistance.getValue());

            if(hitResult != null) {
                BlockPos blockPos = hitResult.getBlockPos();
                Direction face = hitResult.getSide();

                PlayerActionC2SPacket startBreaking = new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                        blockPos,
                        face
                );

                PlayerActionC2SPacket stopBreaking = new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        blockPos,
                        face
                );

                mc.getNetworkHandler().sendPacket(startBreaking);
                mc.getNetworkHandler().sendPacket(stopBreaking);
            }
        }

        if(mc.options.attackKey.isPressed() || autoAttack.getValue()) {
            double maxReach = autoAttack.getValue() ? autoAttackRange.getValue() :
                    (noLimitReach.getValue() ? Double.MAX_VALUE : reachDistance.getValue());

            Entity target = findTarget(maxReach);

            if(target != null && (mc.options.attackKey.isPressed() || autoAttack.getValue())) {
                attackEntity(target);
            }
        }
    };

    /**
     * Finds the closest entity to attack based on the player's look vector
     */
    private Entity findTarget(double maxDistance) {
        if(mc.player == null || mc.world == null) return null;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d lookVec = mc.player.getRotationVec(1.0f);

        List<Entity> possibleTargets = new ArrayList<>();

        for(Entity entity : mc.world.getEntities()) {
            if(entity == mc.player) continue;
            if(!(entity instanceof LivingEntity)) continue;

            double distance = entity.getPos().distanceTo(mc.player.getPos());
            if(distance > maxDistance) continue;

            if(!attackThroughWalls.getValue()) {
                BlockHitResult blockHitResult = RaycastUtility.raycast(
                        mc,
                        eyePos.distanceTo(entity.getPos().add(0, entity.getHeight() / 2, 0))
                );

                if(blockHitResult != null) continue;
            }

            Vec3d toEntity = entity.getPos().add(0, entity.getHeight() / 2, 0).subtract(eyePos).normalize();
            double dot = lookVec.dotProduct(toEntity);

            if(dot > 0.5) {
                possibleTargets.add(entity);
            }
        }

        possibleTargets.sort(Comparator.comparingDouble(e -> e.getPos().distanceTo(mc.player.getPos())));

        return possibleTargets.isEmpty() ? null : possibleTargets.getFirst();
    }

    /**
     * Attacks an entity by sending a packet directly to the server
     */
    private void attackEntity(Entity entity) {
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(
                entity,
                mc.player.isSneaking()
        );

        mc.getNetworkHandler().sendPacket(packet);

        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @SuppressWarnings("unused")
    private final IEventListener<ReachEvent> reachEvent = event -> {
        if(isEnabled()) {
            if(noLimitReach.getValue()) {
                event.setReachDistance(Double.MAX_VALUE);
            } else {
                event.setReachDistance(reachDistance.getValue());
            }
        }
    };
}
