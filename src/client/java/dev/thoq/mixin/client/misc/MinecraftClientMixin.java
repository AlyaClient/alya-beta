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

package dev.thoq.mixin.client.misc;

import dev.thoq.RyeClient;
import dev.thoq.event.impl.TickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin {

    @Shadow
    public abstract Window getWindow();

    @Inject(at = @At("HEAD"), method = "render")
    public void getWindowTitle(boolean tick, CallbackInfo ci) {
        String ryeState = RyeClient.getState();

        String title = String.format(
                "%s %s %s (%s)",
                RyeClient.getName(),
                RyeClient.getEdition(),
                RyeClient.getType(),
                RyeClient.getBuildNumber()
        );

        title += switch(ryeState) {
            case "loading" -> " - Injecting...";
            case "mainMenu" -> " - Main Menu";
            case "inGame" -> " - In Game";
            default -> "";
        };

        getWindow().setTitle(title);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onPreTick(CallbackInfo ci) {
        TickEvent event = new TickEvent(ci);

        RyeClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo ci) {
        TickEvent event = new TickEvent(ci);

        event.setPost();

        RyeClient.getEventBus().dispatch(event);
    }
}
