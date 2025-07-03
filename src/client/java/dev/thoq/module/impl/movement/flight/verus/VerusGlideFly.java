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

package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;

public class VerusGlideFly extends SubModule {

    private static int timeRunning = 0;
    private static boolean messageSent = false;

    private final BooleanSetting clip = new BooleanSetting("Clip", "guh", true);

    public VerusGlideFly(final Module parent) {
        super("VerusGlide", parent);
        this.addSettings(this.clip);
    }

    private final IEventListener<MotionEvent> onMotion = event -> {
        if(!event.isPre()) return;
        if(mc.player == null) return;
        if(!messageSent) {
            ChatUtility.sendWarning("This fly does *NOT* new Verus, only old cracked versions!");
            messageSent = true;
        }

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();

        timeRunning++;

        if(clip.getValue() && timeRunning >= 80) {
            mc.player.setPosition(posX, posY + 1, posZ);
            timeRunning = 0;
        }

        MoveUtility.setMotionY(-0.02);
        MoveUtility.setSpeed(0.1);
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
