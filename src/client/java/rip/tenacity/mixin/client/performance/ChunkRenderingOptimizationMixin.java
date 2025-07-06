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
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(WorldRenderer.class)
public class ChunkRenderingOptimizationMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ChunkBuilder chunkBuilder;
    @Shadow
    private int viewDistance;

    @Unique
    private long lastOptimization = 0;
    @Unique
    private static final long OPTIMIZATION_INTERVAL = 1000;
    @Unique
    private static final long FPS_CHECK_INTERVAL = 500;
    @Unique
    private long lastFpsCheck = 0;
    @Unique
    private int frameCount = 0;
    @Unique
    private double currentFps = 60.0;
    @Unique
    private boolean lowFpsMode = false;
    @Unique
    private final Set<ChunkPos> priorityChunks = ConcurrentHashMap.newKeySet();
    @Unique
    private long lastChunkCleanup = 0;
    @Unique
    private static final long CHUNK_CLEANUP_INTERVAL = 5000;
    @Unique
    private static final int MINIMUM_CHUNKS_AROUND_PLAYER = 5;

    @Inject(method = "render", at = @At("HEAD"))
    private void optimizeChunkBuilding(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;


        long currentTime = System.currentTimeMillis();
        frameCount++;

        if(currentTime - lastFpsCheck > FPS_CHECK_INTERVAL) {
            currentFps = frameCount / ((currentTime - lastFpsCheck) / 1000.0);
            frameCount = 0;
            lastFpsCheck = currentTime;

            lowFpsMode = currentFps < 25.0;
        }

        if(currentTime - lastOptimization > OPTIMIZATION_INTERVAL) {
            lastOptimization = currentTime;
            optimizeChunkSettings();
        }

        if(currentTime - lastChunkCleanup > CHUNK_CLEANUP_INTERVAL) {
            lastChunkCleanup = currentTime;
            priorityChunks.clear();
        }
    }

    @Unique
    private void optimizeChunkSettings() {
        if(chunkBuilder == null || client.player == null)
            return;

        BlockPos playerPos = client.player.getBlockPos();
        ChunkPos playerChunk = new ChunkPos(playerPos);

        priorityChunks.clear();
        for(int x = -MINIMUM_CHUNKS_AROUND_PLAYER; x <= MINIMUM_CHUNKS_AROUND_PLAYER; x++) {
            for(int z = -MINIMUM_CHUNKS_AROUND_PLAYER; z <= MINIMUM_CHUNKS_AROUND_PLAYER; z++) {
                priorityChunks.add(new ChunkPos(playerChunk.x + x, playerChunk.z + z));
            }
        }

        if(lowFpsMode)
            ryeClient$setChunkBuilderPriority(Thread.MIN_PRIORITY + 1);
        else
            ryeClient$setChunkBuilderPriority(Thread.NORM_PRIORITY);
    }

    @Unique
    private void ryeClient$setChunkBuilderPriority(int priority) {
        try {
            Thread.currentThread().setPriority(Math.max(Thread.MIN_PRIORITY,
                    Math.min(Thread.MAX_PRIORITY, priority)));
        } catch(Exception ignored) {
        }
    }

    @Inject(method = "setupTerrain", at = @At("HEAD"))
    private void optimizeTerrainSetup(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(lowFpsMode && client.player != null) {
            BlockPos playerPos = client.player.getBlockPos();
            ryeClient$updatePriorityChunks(playerPos);
        }
    }

    @Unique
    private void ryeClient$updatePriorityChunks(BlockPos playerPos) {
        ChunkPos playerChunk = new ChunkPos(playerPos);

        for(int x = -MINIMUM_CHUNKS_AROUND_PLAYER; x <= MINIMUM_CHUNKS_AROUND_PLAYER; x++) {
            for(int z = -MINIMUM_CHUNKS_AROUND_PLAYER; z <= MINIMUM_CHUNKS_AROUND_PLAYER; z++) {
                priorityChunks.add(new ChunkPos(playerChunk.x + x, playerChunk.z + z));
            }
        }
    }

    @Inject(method = "scheduleChunkRender*", at = @At("HEAD"), cancellable = true)
    private void optimizeChunkRebuild(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(lowFpsMode && client.player != null) {
            if(currentFps < 15.0) {
                if(Math.random() < 0.15) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void optimizeWeatherRendering(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(lowFpsMode && currentFps < 15.0) {
            ci.cancel();
        }
    }

    @Inject(method = "updateChunks", at = @At("TAIL"))
    private void optimizeChunkSorting(CallbackInfo ci) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(client.player == null)
            return;

        BlockPos playerPos = client.player.getBlockPos();
        ryeClient$maintainPriorityChunks(playerPos);
    }

    @Unique
    private void ryeClient$maintainPriorityChunks(BlockPos playerPos) {
        ChunkPos playerChunk = new ChunkPos(playerPos);

        priorityChunks.removeIf(chunkPos -> {
            double distance = Math.sqrt(
                    Math.pow(chunkPos.x - playerChunk.x, 2) +
                            Math.pow(chunkPos.z - playerChunk.z, 2)
            );
            return distance > viewDistance + 4;
        });
    }

    @Inject(method = "getChunkBuilder", at = @At("RETURN"))
    private void implementDynamicLOD(CallbackInfoReturnable<ChunkBuilder> cir) {
        if(!TenacityClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkRendering()) return;

        if(lowFpsMode && cir.getReturnValue() != null && currentFps < 20)
            ryeClient$adjustChunkLOD();
    }

    @Unique
    private void ryeClient$adjustChunkLOD() {
        if(currentFps < 15) {
            try {
                Thread.sleep(10 / 100);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}