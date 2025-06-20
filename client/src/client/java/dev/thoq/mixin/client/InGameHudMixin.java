package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleRepository;
import dev.thoq.module.impl.visual.ClickGUIModule;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        RyeClient.setState("inGame");

        ModuleRepository.getInstance().getEnabledModules().forEach(module -> module.render(context));
    }
}
