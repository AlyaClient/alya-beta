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

package rip.tenacity.mixin.client.performance;

import rip.tenacity.TenacityClient;
import rip.tenacity.module.impl.visual.PerformanceModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(WorldRenderer.class)
public class EntityCullingOptimizationMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    @Shadow
    private Frustum frustum;

    @Unique
    private final Set<Entity> ryeClient$culledEntities = ConcurrentHashMap.newKeySet();
    @Unique
    private final Set<Entity> ryeClient$visibleEntities = ConcurrentHashMap.newKeySet();
    @Unique
    private long ryeClient$lastCullUpdate = 0;
    @Unique
    private static final long CULL_UPDATE_INTERVAL = 100;
    @Unique
    private static final double MAX_ENTITY_DISTANCE = 128.0;
    @Unique
    private static final double IMPORTANT_ENTITY_DISTANCE = 256.0;
    @Unique
    private long ryeClient$lastFpsCheck = 0;
    @Unique
    private int ryeClient$frameCount = 0;
    @Unique
    private double ryeClient$currentFps = 60.0;
    @Unique
    private boolean ryeClient$aggressiveCulling = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void updateEntityCulling(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeEntityCulling()) return;

        long currentTime = System.currentTimeMillis();
        ryeClient$frameCount++;

        if(currentTime - ryeClient$lastFpsCheck > 1000) {
            ryeClient$currentFps = ryeClient$frameCount / ((currentTime - ryeClient$lastFpsCheck) / 1000.0);
            ryeClient$frameCount = 0;
            ryeClient$lastFpsCheck = currentTime;
            ryeClient$aggressiveCulling = ryeClient$currentFps < 30.0;
        }

        if(currentTime - ryeClient$lastCullUpdate > CULL_UPDATE_INTERVAL) {
            ryeClient$lastCullUpdate = currentTime;
            ryeClient$updateEntityCulling();
        }
    }

    @Unique
    private void ryeClient$updateEntityCulling() {
        if(world == null || client.player == null) return;

        ryeClient$culledEntities.clear();
        ryeClient$visibleEntities.clear();

        Vec3d playerPos = client.player.getPos();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();

        for(Entity entity : world.getEntities()) {
            if(entity == client.player) continue;
            if(ryeClient$shouldAlwaysRender(entity)) {
                ryeClient$visibleEntities.add(entity);
                continue;
            }

            if(ryeClient$shouldCullEntity(entity, playerPos, cameraPos)) {
                ryeClient$culledEntities.add(entity);
            } else {
                ryeClient$visibleEntities.add(entity);
            }
        }
    }

    @Unique
    private boolean ryeClient$shouldAlwaysRender(Entity entity) {
        if(entity instanceof PlayerEntity) return true;

        if(Objects.requireNonNull(client.player).getVehicle() == entity) return true;
        if(client.player.isRiding() && client.player.getRootVehicle() == entity) return true;

        return entity.hasCustomName();
    }

    @Unique
    private boolean ryeClient$shouldCullEntity(Entity entity, Vec3d playerPos, Vec3d cameraPos) {
        Vec3d entityPos = entity.getPos();
        double distanceToPlayer = playerPos.distanceTo(entityPos);
        double distanceToCamera = cameraPos.distanceTo(entityPos);

        double maxDistance = ryeClient$getMaxRenderDistance(entity);
        if(distanceToPlayer > maxDistance && distanceToCamera > maxDistance) {
            return true;
        }

        if(frustum != null && !ryeClient$isEntityInFrustum(entity)) {
            return true;
        }

        if(ryeClient$aggressiveCulling && ryeClient$isEntityOccluded(entity, cameraPos)) {
            return true;
        }

        return ryeClient$shouldPerformanceCull(entity, distanceToPlayer);
    }

    @Unique
    private double ryeClient$getMaxRenderDistance(Entity entity) {
        if(entity instanceof PlayerEntity) return IMPORTANT_ENTITY_DISTANCE;
        if(entity instanceof VehicleEntity) return IMPORTANT_ENTITY_DISTANCE;
        if(entity instanceof ItemFrameEntity) return IMPORTANT_ENTITY_DISTANCE * 0.5;
        if(entity instanceof ArmorStandEntity) return MAX_ENTITY_DISTANCE * 0.75;

        if(ryeClient$aggressiveCulling) {
            return MAX_ENTITY_DISTANCE * 0.6;
        }

        return MAX_ENTITY_DISTANCE;
    }

    @Unique
    private boolean ryeClient$isEntityInFrustum(Entity entity) {
        if(frustum == null) return true;

        Box boundingBox = entity.getBoundingBox();
        return frustum.isVisible(boundingBox);
    }

    @Unique
    private boolean ryeClient$isEntityOccluded(Entity entity, Vec3d cameraPos) {
        if(world == null) return false;

        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2, 0);

        return !world.raycast(new net.minecraft.world.RaycastContext(
                cameraPos,
                entityPos,
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                Objects.requireNonNull(client.player)
        )).getType().equals(net.minecraft.util.hit.HitResult.Type.MISS);
    }

    @Unique
    private boolean ryeClient$shouldPerformanceCull(Entity entity, double distance) {
        if(!ryeClient$aggressiveCulling) return false;

        if(ryeClient$currentFps < 15) {
            if(!(entity instanceof PlayerEntity) &&
                    !(entity instanceof VehicleEntity) &&
                    distance > MAX_ENTITY_DISTANCE * 0.3) {
                return true;
            }
        }

        if(ryeClient$currentFps < 20) {
            return entity.getHeight() < 0.5 && entity.getWidth() < 0.5 && distance > 32;
        }

        return false;
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void cullEntitiesInShouldRender(Entity entity, double cameraX, double cameraY, double cameraZ, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(ryeClient$culledEntities.contains(entity)) {
            ci.cancel();
        }
    }
}