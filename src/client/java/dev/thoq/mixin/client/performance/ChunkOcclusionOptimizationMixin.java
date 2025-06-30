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

package dev.thoq.mixin.client.performance;

import dev.thoq.RyeClient;
import dev.thoq.module.impl.visual.PerformanceModule;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkOcclusionData.class)
public class ChunkOcclusionOptimizationMixin {
    @Unique private static final ConcurrentHashMap<Integer, Boolean> ryeClient$occlusionCache = new ConcurrentHashMap<>();
    @Unique private static long ryeClient$lastCacheClear = System.currentTimeMillis();
    @Unique private static final long CACHE_CLEAR_INTERVAL = 10000;

    @Inject(method = "isVisibleThrough", at = @At("HEAD"), cancellable = true)
    private void cacheOcclusionResults(CallbackInfoReturnable<Boolean> cir) {
        if(!RyeClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkOcclusion()) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - ryeClient$lastCacheClear > CACHE_CLEAR_INTERVAL) {
            ryeClient$occlusionCache.clear();
            ryeClient$lastCacheClear = currentTime;
        }

        int hashKey = this.hashCode();

        Boolean cachedResult = ryeClient$occlusionCache.get(hashKey);
        if (cachedResult != null) {
            cir.setReturnValue(cachedResult);
        }
    }

    @Inject(method = "isVisibleThrough", at = @At("TAIL"))
    private void cacheOcclusionResult(CallbackInfoReturnable<Boolean> cir) {
        if(!RyeClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkOcclusion()) return;

        int hashKey = this.hashCode();
        ryeClient$occlusionCache.put(hashKey, cir.getReturnValue());
    }
}