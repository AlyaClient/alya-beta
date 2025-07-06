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

package rip.tenacity.module.impl.movement.flight.verus;

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.misc.ChatUtility;
import rip.tenacity.utilities.player.MoveUtility;

public class VerusGlideFly extends SubModule {

    private static int timeRunning = 0;
    private static boolean messageSent = false;

    private final BooleanSetting clip = new BooleanSetting("Clip", "guh", true);

    public VerusGlideFly(final Module parent) {
        super("VerusGlide", parent);
        this.addSettings(this.clip);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(mc.player == null) return;

        if(mc.player.isOnGround()) {
            ChatUtility.sendError("Please be in air before toggling!");
            return;
        }

        if(!messageSent) {
            ChatUtility.sendWarning("This fly may not work on new Verus (its iffy), it does work on old cracked versions!");
            messageSent = true;
        }

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();

        timeRunning++;

        if(clip.getValue() && timeRunning >= 90) {
            mc.player.setPosition(posX, posY + 2, posZ);
            timeRunning = 0;
        }

        MoveUtility.setMotionY(-0.02);
        MoveUtility.setSpeed(0.3, true);
    };

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    public void reset() {
        messageSent = false;
    }
}
