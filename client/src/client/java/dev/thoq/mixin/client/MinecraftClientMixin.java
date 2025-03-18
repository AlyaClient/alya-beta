package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
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

    @Shadow public abstract Window getWindow();

    @Inject(at = @At("HEAD"), method = "render")
    public void getWindowTitle(boolean tick, CallbackInfo ci) {
        String ryeState = RyeClient.getState();

        String title = String.format(
                "%s %s %s %s",
                RyeClient.getName(),
                RyeClient.getEdition(),
                RyeClient.getType(),
                RyeClient.getBuildNumber()
        );

        title += switch(ryeState) {
            case "loading" -> " - Injecting...";
            case "mainMenu" -> " - Main Menu";
            case "inGame" -> " - In Game";
            default -> "";
        };

        getWindow().setTitle(title);
    }
}
