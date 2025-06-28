/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.player.fastplace;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.PacketReceiveEvent;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.misc.ChatUtility;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;

@SuppressWarnings("CallToPrintStackTrace")
public class FastPlaceModule extends Module {
    private Field itemUseCooldownField;

    public FastPlaceModule() {
        super("FastPlace", "Helicopter helicopter", ModuleCategory.PLAYER);
        try {
            itemUseCooldownField = MinecraftClient.class.getDeclaredField("itemUseCooldown");
            itemUseCooldownField.setAccessible(true);
        } catch(NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || itemUseCooldownField == null) return;

        try {
            itemUseCooldownField.set(mc, 0);
        } catch(IllegalAccessException ex) {
            ChatUtility.sendError("Failed to set block place cooldown!");
        }
    };

    @Override
    protected void onDisable() {
        if(mc.player == null || itemUseCooldownField == null) return;
        try {
            itemUseCooldownField.set(mc, 4);
        } catch(IllegalAccessException ex) {
            ChatUtility.sendError("Failed to reset block place cooldown!");
        }
    }
}