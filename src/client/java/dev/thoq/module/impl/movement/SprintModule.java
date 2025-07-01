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

package dev.thoq.module.impl.movement;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

@SuppressWarnings("FieldCanBeLocal")
public class SprintModule extends Module {
    private final BooleanSetting omniSprint = new BooleanSetting("OmniSprint", "Sprint in all directions", false);

    public SprintModule() {
    super("Sprint", "Sprint", "Makes player less american", ModuleCategory.MOVEMENT);
        addSetting(omniSprint);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(mc.getNetworkHandler() == null || mc.player == null || mc.options == null || !event.isPre()) return;

        boolean omniSprintEnabled = ((BooleanSetting) getSetting("OmniSprint")).getValue();
        boolean movingForwards = mc.options.forwardKey.isPressed();

        if(MoveUtility.isMoving()) {
            if(omniSprintEnabled) {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                mc.player.setSprinting(true);
            } else if(movingForwards) {
                mc.player.setSprinting(true);
            }
        }
    };

    @Override
    protected void onDisable() {
        if(mc.player == null) return;
        mc.player.setSprinting(false);
    }

}