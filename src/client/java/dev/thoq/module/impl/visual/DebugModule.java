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

package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.event.impl.PacketReceiveEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.misc.ChatUtility;

@SuppressWarnings("FieldCanBeLocal")
public class DebugModule extends Module {
    private final BooleanSetting packetLog = new BooleanSetting("Packets", "Log all packets to console, may lag game", false);
    private final BooleanSetting motionLog = new BooleanSetting("Motion", "Log motion values to console, may lag game", false);

    public DebugModule() {
        super("Debug", "Shows debug info-only of intrest to developers", ModuleCategory.VISUAL);

        addSetting(packetLog);
        addSetting(motionLog);

        if(RyeClient.getType().equals("Development") || RyeClient.getType().equals("Beta")) {
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
