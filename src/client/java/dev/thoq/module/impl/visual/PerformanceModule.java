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

package dev.thoq.module.impl.visual;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class PerformanceModule extends Module {

    public PerformanceModule() {
        super("Performance", "Improves performance", ModuleCategory.VISUAL);

        BooleanSetting entityCullingOptimization = new BooleanSetting("Entity Culling", "Hides invisible entities", true);
        BooleanSetting chunkRenderingOptimization = new BooleanSetting("Chunk Rendering", "Optimize the chunk renderer", true);

        addSetting(chunkRenderingOptimization);
        addSetting(entityCullingOptimization);
    }

    public boolean shouldOptimizeChunkRendering() {
        return ((BooleanSetting) getSetting("Chunk Rendering")).getValue();
    }

    public boolean shouldOptimizeEntityCulling() {
        return ((BooleanSetting) getSetting("Entity Culling")).getValue();
    }
}
