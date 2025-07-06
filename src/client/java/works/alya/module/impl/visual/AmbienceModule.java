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

package works.alya.module.impl.visual;

import works.alya.config.setting.impl.NumberSetting;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AmbienceModule extends Module {
    private final NumberSetting<Integer> time = new NumberSetting<>("Time", "Time of day", 12, 0, 24);
    private ScheduledExecutorService executor;

    public AmbienceModule() {
        super("Ambience", "Change the time of day", ModuleCategory.VISUAL);

        addSetting(time);
    }

    @Override
    protected void onEnable() {
        if(mc.world == null) return;

        if(executor == null || executor.isShutdown()) {
            executor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        }

        long timeOfDay = time.getValue() * 1000;

        executor.schedule(() -> {
            if(mc.world != null) {
                long currentTime = mc.world.getTime();
                mc.world.setTime(currentTime, timeOfDay, false);
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void onDisable() {
        if(executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}