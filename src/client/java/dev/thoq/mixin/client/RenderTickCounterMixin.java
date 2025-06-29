/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.mixin.client;

import dev.thoq.utilities.player.TimerUtility;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.Dynamic.class)
public abstract class RenderTickCounterMixin {
    @Shadow
    private float dynamicDeltaTicks;

    @Inject(at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastTimeMillis:J",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0), method = "beginRenderTick(J)I")
    public void onBeginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        dynamicDeltaTicks *= TimerUtility.getTimerSpeed();
    }
}
