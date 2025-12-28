/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.visual;

import works.alya.AlyaClient;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.event.impl.PacketReceiveEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.misc.ChatUtility;

@SuppressWarnings("FieldCanBeLocal")
public final class DebugModule extends Module {
    private final BooleanSetting packetLog = new BooleanSetting("Packets", "Log all packets to console, may lag game", false);
    private final BooleanSetting motionLog = new BooleanSetting("Motion", "Log motion values to console, may lag game", false);

    public DebugModule() {
        super("Debug", "Shows debug info-only of intrest to developers", ModuleCategory.VISUAL);

        addSetting(packetLog);
        addSetting(motionLog);

        if(AlyaClient.getType().equals("Development") || AlyaClient.getType().equals("Beta")) {
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
