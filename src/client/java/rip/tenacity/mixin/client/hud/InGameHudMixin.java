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

package rip.tenacity.mixin.client.hud;

import rip.tenacity.TenacityClient;
import rip.tenacity.event.impl.Render2DEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        TenacityClient.setState("inGame");

        Render2DEvent event = new Render2DEvent(context);

        TenacityClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject( method = "render", at = @At("TAIL"))
    public void onHudRenderPost(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        TenacityClient.setState("inGame");

        Render2DEvent event = new Render2DEvent(context);

        event.setPost();

        TenacityClient.getEventBus().dispatch(event);
    }
}
