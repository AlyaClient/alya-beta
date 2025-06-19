package dev.thoq.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityJumpAccessor {
    @Accessor("jumpingCooldown")
    void setJumpingCooldown(int jumpingCooldown);
}
