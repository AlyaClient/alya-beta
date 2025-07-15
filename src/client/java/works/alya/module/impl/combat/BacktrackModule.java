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
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.List;

/**
 * TODO: Make it better
 */
public class BacktrackModule extends Module {

    /**
     * Settings for controlling backtrack behavior
     */
    private static final NumberSetting<Integer> backtrackTime = new NumberSetting<>("BacktrackTime", "Maximum time to backtrack in milliseconds", 200, 0, 1000);
    private static final BooleanSetting playersOnly = new BooleanSetting("PlayersOnly", "Only track player entities", true);
    private static final NumberSetting<Double> maxDistance = new NumberSetting<>("MaxDistance", "Maximum distance to track entities", 15.0, 5.0, 30.0);
    private static final BooleanSetting attackAtBacktrack = new BooleanSetting("AttackAtBacktrack", "Attack entities at their backtracked positions", true);
    private static final NumberSetting<Double> attackRange = new NumberSetting<>("AttackRange", "Maximum attack range for backtracked positions", 4.0, 3.0, 8.0);
    private final Map<Integer, List<PositionData>> positionHistory = new HashMap<>();

    public BacktrackModule() {
        super("Backtrack", "Allows hitting players at their previous positions when they move out of range", ModuleCategory.COMBAT);

        addSetting(backtrackTime);
        addSetting(playersOnly);
        addSetting(maxDistance);
        addSetting(attackAtBacktrack);
        addSetting(attackRange);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        positionHistory.clear();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        positionHistory.clear();
    }

    /**
     * Tracks entity positions and handles backtracking
     */
    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.world == null || mc.player == null) return;

        if(event.isPre()) {
            trackEntityPositions();
            cleanupOldPositions();

            if(mc.options.attackKey.isPressed() && attackAtBacktrack.getValue()) {
                Entity target = findBestBacktrackedTarget();
                if(target != null) {
                    attackEntity(target);
                }
            }
        }
    };

    /**
     * Tracks the positions of all relevant entities in the world
     */
    private void trackEntityPositions() {
        if(mc.world == null || mc.player == null) return;

        double trackDistance = maxDistance.getValue();

        for(Entity entity : mc.world.getEntities()) {
            if(!(entity instanceof LivingEntity) || entity == mc.player) continue;
            if(playersOnly.getValue() && !(entity instanceof PlayerEntity)) continue;
            if(entity.squaredDistanceTo(mc.player) > trackDistance * trackDistance) continue;

            addPositionToHistory(entity.getId(), entity.getPos());
        }
    }

    /**
     * Adds a position to the entity's position history
     */
    private void addPositionToHistory(int entityId, Vec3d position) {
        long currentTime = System.currentTimeMillis();

        List<PositionData> positions = positionHistory.computeIfAbsent(entityId, k -> new ArrayList<>());

        if(positions.isEmpty() || positions.getLast().position.squaredDistanceTo(position) > 0.0001) {
            positions.add(new PositionData(position, currentTime));
        }
    }

    /**
     * Cleans up old position data that exceeds the backtrack time
     */
    private void cleanupOldPositions() {
        long currentTime = System.currentTimeMillis();
        long maxAge = backtrackTime.getValue();

        positionHistory.forEach((entityId, positions) -> positions.removeIf(data -> currentTime - data.timestamp > maxAge));

        positionHistory.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * Finds the best entity to attack based on backtracked positions
     */
    private Entity findBestBacktrackedTarget() {
        if(mc.player == null || mc.world == null) return null;

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d lookVec = mc.player.getRotationVec(1.0f);
        double maxAttackRange = attackRange.getValue();

        Entity bestTarget = null;
        double bestScore = Double.MAX_VALUE;
        Vec3d bestPosition = null;

        for(Map.Entry<Integer, List<PositionData>> entry : positionHistory.entrySet()) {
            int entityId = entry.getKey();
            List<PositionData> positions = entry.getValue();

            Entity entity = mc.world.getEntityById(entityId);
            if(!(entity instanceof LivingEntity) || entity == mc.player) continue;

            for(int i = positions.size() - 1; i >= 0; i--) {
                PositionData posData = positions.get(i);
                Vec3d historicalPos = posData.position;

                double historicalDistance = eyePos.distanceTo(historicalPos.add(0, entity.getHeight() / 2, 0));

                if(historicalDistance <= maxAttackRange) {
                    Vec3d toEntity = historicalPos.add(0, entity.getHeight() / 2, 0).subtract(eyePos).normalize();
                    double dot = lookVec.dotProduct(toEntity);

                    if(dot > 0.5) {
                        long age = System.currentTimeMillis() - posData.timestamp;
                        double score = historicalDistance * (1.0 - dot) + (age / 1000.0);

                        if(score < bestScore) {
                            bestScore = score;
                            bestTarget = entity;
                            bestPosition = historicalPos;
                        }
                    }
                }
            }
        }

        if(bestTarget != null) {
            double distance = eyePos.distanceTo(bestPosition);
            setPrefix(String.format("%.1f", distance));
        } else {
            setPrefix("");
        }

        return bestTarget;
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

    /**
     * Class to store position data with timestamp
     */
    private record PositionData(Vec3d position, long timestamp) {
    }
}