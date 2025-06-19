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
