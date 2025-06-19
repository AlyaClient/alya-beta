package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
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

        String name = RyeClient.getName();
        String edition = RyeClient.getEdition();
        String type = RyeClient.getType();
        String buildNumber = RyeClient.getBuildNumber();
        String fps = RyeClient.getFps();
        String bps = RyeClient.getBps();

        TextRendererUtility.renderText(
                context,
                String.format(
                        "%s %s %s %s (FPS: %s, BPS: %s)",
                        name,
                        edition,
                        type,
                        buildNumber,
                        fps,
                        bps
                ),
                ColorUtility.Colors.LAVENDER,
                1,
                2,
                true
        );

        ModuleRepository repository = ModuleRepository.getInstance();
        ClickGUIModule clickGUI = (ClickGUIModule) repository.getModuleByName("ClickGUI");
        if(clickGUI != null) {
            clickGUI.render(context);
        }
    }
}
