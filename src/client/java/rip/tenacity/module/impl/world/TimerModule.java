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

package rip.tenacity.module.impl.world;

import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.player.TimerUtility;

public class TimerModule extends Module {
    private final NumberSetting<Float> timerSpeed = new NumberSetting<>("Speed", "Game speed multiplier", 1.0f, 0.1f, 10.0f);

    public TimerModule() {
        super("Timer", "Allows you to change the game speed", ModuleCategory.WORLD);

        addSetting(timerSpeed);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(timerSpeed.getValue() != TimerUtility.getTimerSpeed()) {
            TimerUtility.setTimerSpeed(timerSpeed.getValue());
        }
    };

    @Override
    protected void onDisable() {
        TimerUtility.resetTimer();
    }
}
