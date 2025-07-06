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

package rip.tenacity.module.impl.visual;

import rip.tenacity.TenacityClient;
import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.event.impl.PacketReceiveEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.misc.ChatUtility;

@SuppressWarnings("FieldCanBeLocal")
public class DebugModule extends Module {
    private final BooleanSetting packetLog = new BooleanSetting("Packets", "Log all packets to console, may lag game", false);
    private final BooleanSetting motionLog = new BooleanSetting("Motion", "Log motion values to console, may lag game", false);

    public DebugModule() {
        super("Debug", "Shows debug info-only of intrest to developers", ModuleCategory.VISUAL);

        addSetting(packetLog);
        addSetting(motionLog);

        if(TenacityClient.getType().equals("Development") || TenacityClient.getType().equals("Beta")) {
            this.setEnabled(true);
        }
    }

    @SuppressWarnings("unused")
    private final IEventListener<PacketReceiveEvent> packetEvent = event -> {
        boolean packetLogEnabled = ((BooleanSetting) getSetting("Packets")).getValue();

        if(packetLogEnabled)
            ChatUtility.sendDebug(event.getPacket().toString());
    };

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!motionLog.getValue() || mc.player == null) return;

        ChatUtility.sendDebug("X=" + mc.player.getVelocity().x);
        ChatUtility.sendDebug("Y=" + mc.player.getVelocity().y);
        ChatUtility.sendDebug("Z=" + mc.player.getVelocity().z);
    };
}
