package dev.thoq.mixin.client;

import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin {
    
    @Shadow
    private float dynamicDeltaTicks;
    
    @Inject(method = "beginRenderTick(J)I", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastTimeMillis:J"))
    private void modifyDeltaTicks(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        float timerSpeed = TimerUtility.getTimerSpeed();
        this.dynamicDeltaTicks *= timerSpeed;
    }
}