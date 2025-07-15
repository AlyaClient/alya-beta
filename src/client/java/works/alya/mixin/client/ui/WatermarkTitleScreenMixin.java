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

package works.alya.mixin.client.ui;

import net.minecraft.util.Identifier;
import works.alya.AlyaClient;
import works.alya.utilities.render.ColorUtility;
import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.TextRendererUtility;
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

    @Shadow
    @Nullable
    private SplashTextRenderer splashText;

    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        AlyaClient.setState("mainMenu");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IF)V"))
    private void renderAlyaLogo(LogoDrawer logoDrawer, DrawContext context, int width, float alpha) {
        Identifier logo = Identifier.of("alya", "icons/icon.png");

        int logoWidth = 64;
        int logoHeight = 64;
        int x = (context.getScaledWindowWidth() / 2) - (logoWidth / 2);
        int y = (context.getScaledWindowHeight() / 2) - logoHeight - (context.getScaledWindowHeight() / 5);

        RenderUtility.drawImage(logo, x, y, logoWidth, logoHeight, logoWidth, logoHeight, context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void replaceVersionText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, String text, int x, int y, int color) {
        String usersOnline = String.format("%s online", AlyaClient.getUsersOnline());

        TextRendererUtility.renderText(context, usersOnline, ColorUtility.Colors.WHITE, x, y, false);
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