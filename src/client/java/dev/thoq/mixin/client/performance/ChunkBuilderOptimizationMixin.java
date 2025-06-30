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
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderOptimizationMixin {

    @Unique
    private final AtomicInteger ryeClient$buildsThisSecond = new AtomicInteger(0);
    @Unique
    private long ryeClient$lastResetTime = System.currentTimeMillis();
    @Unique
    private static final int MAX_BUILDS_PER_SECOND = 30;

    @Inject(method = "scheduleRunTasks", at = @At("HEAD"), cancellable = true)
    private void limitChunkBuilds(CallbackInfo ci) {
        if(!RyeClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkBuilder()) return;

        long currentTime = System.currentTimeMillis();

        if(currentTime - ryeClient$lastResetTime > 1000) {
            ryeClient$buildsThisSecond.set(0);
            ryeClient$lastResetTime = currentTime;
        }

        if(ryeClient$buildsThisSecond.get() >= MAX_BUILDS_PER_SECOND) {
            ci.cancel();
            return;
        }

        ryeClient$buildsThisSecond.incrementAndGet();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void resetCounters(CallbackInfo ci) {
        if(!RyeClient.INSTANCE.getModuleRepository().getModule(PerformanceModule.class).shouldOptimizeChunkBuilder()) return;

        ryeClient$buildsThisSecond.set(0);
    }
}