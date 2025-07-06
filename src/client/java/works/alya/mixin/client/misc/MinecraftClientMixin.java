/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.mixin.client.misc;

import works.alya.AlyaClient;
import works.alya.event.impl.TickEvent;
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
        String ryeState = AlyaClient.getState();

        String title = String.format(
                "%s %s %s (%s)",
                AlyaClient.getName(),
                AlyaClient.getEdition(),
                AlyaClient.getType(),
                AlyaClient.getBuildNumber()
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

        AlyaClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo ci) {
        TickEvent event = new TickEvent(ci);

        event.setPost();

        AlyaClient.getEventBus().dispatch(event);
    }
}
