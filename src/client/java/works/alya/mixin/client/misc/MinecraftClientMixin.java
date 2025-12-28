/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
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
    public void getWindowTitle(final boolean tick, final CallbackInfo ci) {
        final String clientState = AlyaClient.getState();

        String title = String.format(
                "%s %s %s (%s)",
                AlyaClient.getName(),
                AlyaClient.getEdition(),
                AlyaClient.getType(),
                AlyaClient.getBuildNumber()
        );

        title += switch(clientState) {
            case "loading" -> " - Injecting...";
            case "mainMenu" -> " - Main Menu";
            case "inGame" -> " - In Game";
            default -> "";
        };

        getWindow().setTitle(title);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onPreTick(final CallbackInfo ci) {
        final TickEvent event = new TickEvent(ci);

        AlyaClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(final CallbackInfo ci) {
        final TickEvent event = new TickEvent(ci);

        event.setPost();

        AlyaClient.getEventBus().dispatch(event);
    }

    @Inject(method = "close", at = @At("HEAD"))
    public void close(final CallbackInfo ci) {
        AlyaClient.backendManager.goOffline();
    }
}
