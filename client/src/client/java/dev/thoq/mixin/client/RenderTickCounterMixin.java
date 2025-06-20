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
