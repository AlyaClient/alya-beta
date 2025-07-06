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

package rip.tenacity.module.impl.movement;

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.player.MoveUtility;
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