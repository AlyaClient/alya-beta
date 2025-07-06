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

package rip.tenacity.mixin.client.ui;

import rip.tenacity.TenacityClient;
import rip.tenacity.utilities.misc.TenacityConstants;
import rip.tenacity.utilities.render.ColorUtility;
import rip.tenacity.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(TitleScreen.class)
public class WatermarkTitleScreenMixin {

    @Shadow
    @Nullable
    private SplashTextRenderer splashText;

    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TenacityClient.setState("mainMenu");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IF)V"))
    private void renderRyeLogo(LogoDrawer logoDrawer, DrawContext context, int width, float alpha) {
        String text = "Tenacity";
        String versionText = TenacityConstants.VERSION;

        int tX = context.getScaledWindowWidth() / 2 - TextRendererUtility.getXlTextWidth(text) / 2;
        int tY = Math.round(context.getScaledWindowHeight() / 4.5f);

        int vX = tX + TextRendererUtility.getXlTextWidth(text);
        int vY = tY - TextRendererUtility.getXlTextHeight() * 3;

        TextRendererUtility.renderXlText(
                context,
                text,
                ColorUtility.Colors.WHITE,
                tX,
                tY,
                false
        );

        TextRendererUtility.renderText(
                context,
                versionText,
                ColorUtility.Colors.WHITE,
                vX,
                vY,
                false
        );
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void replaceVersionText(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer, String text, int x, int y, int color) {
        // we ignore demo, modded and version
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void disableSplashText(CallbackInfo ci) {
        this.splashText = null;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashTextRenderer;render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/client/font/TextRenderer;F)V"))
    private void cancelSplashTextRendering(SplashTextRenderer splashTextRenderer, DrawContext context, int width, net.minecraft.client.font.TextRenderer textRenderer, float alpha) {
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void centerButtons(CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;

        int screenWidth = screen.width;
        int screenHeight = screen.height;
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        List<ButtonWidget> buttons = new ArrayList<>();
        for(Element element : screen.children()) {
            if(element instanceof ButtonWidget button) {
                buttons.add(button);
            }
        }

        if(!buttons.isEmpty()) {
            int totalHeight = 0;
            for(int i = 0; i < buttons.size(); i++) {
                totalHeight += buttons.get(i).getHeight();
                if(i < buttons.size() - 1) {
                    totalHeight += 24;
                }
            }

            int currentY = centerY - (totalHeight / 2);
            for(ButtonWidget button : buttons) {
                int buttonWidth = button.getWidth();
                int newX = centerX - (buttonWidth / 2);

                button.setPosition(newX, currentY);
                currentY += button.getHeight() + 24;
            }
        }
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/AccessibilityOnboardingButtons;createLanguageButton(ILnet/minecraft/client/gui/widget/ButtonWidget$PressAction;Z)Lnet/minecraft/client/gui/widget/TextIconButtonWidget;", shift = At.Shift.AFTER), cancellable = true)
    private void skipLanguageButtonSetup(CallbackInfo ci) {
        ci.cancel();
    }
}