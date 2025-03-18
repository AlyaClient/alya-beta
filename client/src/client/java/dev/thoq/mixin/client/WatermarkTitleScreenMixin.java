package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class WatermarkTitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RyeClient.setState("mainMenu");
    }
}
