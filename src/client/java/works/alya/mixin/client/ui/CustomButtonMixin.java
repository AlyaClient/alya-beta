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

import works.alya.utilities.render.RenderUtility;
import works.alya.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PressableWidget.class)
public class CustomButtonMixin {

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void renderCustomButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        PressableWidget widget = (PressableWidget) (Object) this;

        int x = widget.getX();
        int y = widget.getY();
        int width = widget.getWidth();
        int height = widget.getHeight();
        boolean isHovered = widget.isHovered();
        boolean isActive = widget.active;
        Text message = widget.getMessage();

        int backgroundColor;
        int textColor;

        if(!isActive) {
            backgroundColor = 0x22FFFFFF;
            textColor = 0xFF9AACB8;
        } else if(isHovered) {
            backgroundColor = 0x44FFFFFF;
            textColor = 0xFFFFFFFF;
        } else {
            backgroundColor = 0x11FFFFFF;
            textColor = 0xFFD0DCE8;
        }

        Vector4f radius = new Vector4f(6.0f, 6.0f, 6.0f, 6.0f);

        RenderUtility.drawRoundedRect(context, x, y, width, height, radius, backgroundColor);

        String buttonText = message.getString();
        int textWidth = TextRendererUtility.getTextWidth(buttonText);
        int textHeight = TextRendererUtility.getTextHeight();

        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - textHeight) / 2;

        TextRendererUtility.renderText(context, buttonText, textColor, textX, textY, false);

        ci.cancel();
    }
}