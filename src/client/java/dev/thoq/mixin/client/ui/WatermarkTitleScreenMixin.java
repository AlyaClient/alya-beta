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
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class WatermarkTitleScreenMixin {

    @Shadow @Nullable private SplashTextRenderer splashText;

    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RyeClient.setState("mainMenu");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IF)V"))
    private void renderRyeLogo(LogoDrawer logoDrawer, DrawContext context, int width, float alpha) {
        // don't draw the minecraft logo

        String text = "Rye";
        int x = context.getScaledWindowWidth() / 2 - TextRendererUtility.getXlTextWidth(text) / 2;
        int y = Math.round(context.getScaledWindowHeight() / 4.5f);

        TextRendererUtility.renderXlText(
                context,
                text,
                ColorUtility.Colors.WHITE,
                x,
                y,
                false
        );
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void replaceVersionText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, String text, int x, int y, int color) {
        // String ryeVersion = String.format("%s Client %s %s", RyeClient.getName(), RyeClient.getType(), RyeClient.getEdition());

        // we ignore demo and modded

        // I think I want to stop this
        // its cleaner
        // context.drawTextWithShadow(textRenderer, ryeVersion, x, y, color);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void disableSplashText(CallbackInfo ci) {
        this.splashText = null;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashTextRenderer;render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/client/font/TextRenderer;F)V"))
    private void cancelSplashTextRendering(SplashTextRenderer splashTextRenderer, DrawContext context, int width, net.minecraft.client.font.TextRenderer textRenderer, float alpha) {
    }


    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/AccessibilityOnboardingButtons;createLanguageButton(ILnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Z)Lnet/minecraft/client/gui/widget/TextIconButtonWidget;", shift = At.Shift.AFTER), cancellable = true)
    private void skipLanguageButtonSetup(CallbackInfo ci) {
        ci.cancel();
    }
}