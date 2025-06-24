/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleRepository;
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

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        ModuleRepository.getInstance().getEnabledModules().forEach(Module::preTick);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPostTick(CallbackInfo ci) {
        ModuleRepository.getInstance().getEnabledModules().forEach(Module::postTick);
    }
}
