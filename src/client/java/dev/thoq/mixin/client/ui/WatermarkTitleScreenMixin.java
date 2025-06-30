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

package dev.thoq.mixin.client.ui;

import dev.thoq.RyeClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class WatermarkTitleScreenMixin {

    @Shadow @Nullable private SplashTextRenderer splashText;
    // TODO: Make this a setting
    @Unique public boolean SHOW_SPLASH_TEXTS = false;

    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RyeClient.setState("mainMenu");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IF)V"))
    private void cancelLogoDraw(LogoDrawer logoDrawer, DrawContext context, int width, float alpha) {
        // don't draw the minecraft logo
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void replaceVersionText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, String text, int x, int y, int color) {
        String ryeVersion = String.format("%s Client %s %s", RyeClient.getName(), RyeClient.getType(), RyeClient.getEdition());

        // we ignore demo and modded

        context.drawTextWithShadow(textRenderer, ryeVersion, x, y, color);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modifySplashTexts(CallbackInfo ci) {
        if(!SHOW_SPLASH_TEXTS) this.splashText = null;
    }
}