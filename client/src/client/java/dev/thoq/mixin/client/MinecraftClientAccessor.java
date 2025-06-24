/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("renderTickCounter")
    RenderTickCounter.Dynamic getRenderTickCounter();

    @Accessor("renderTickCounter")
    void setRenderTickCounter(RenderTickCounter.Dynamic renderTickCounter);
}
