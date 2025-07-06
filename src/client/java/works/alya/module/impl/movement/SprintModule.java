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

package works.alya.module.impl.movement;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.player.MoveUtility;
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