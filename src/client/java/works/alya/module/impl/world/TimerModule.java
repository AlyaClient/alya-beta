/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.world;

import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.player.TimerUtility;

public final class TimerModule extends Module {
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
