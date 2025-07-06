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

import rip.tenacity.config.setting.impl.BooleanSetting;
import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.utilities.player.TimerUtility;

public class TickBaseModule extends Module {
    private final NumberSetting<Integer> ticksToAccumulate = new NumberSetting<>("Ticks", "Number of ticks to accumulate", 5, 1, 20);
    private final NumberSetting<Float> freezeSpeed = new NumberSetting<>("Freeze Speed", "Speed during freeze phase", 0.1f, 0.01f, 0.5f);
    private final NumberSetting<Float> releaseSpeed = new NumberSetting<>("Release Speed", "Speed during release phase", 5.0f, 2.0f, 10.0f);
    private final BooleanSetting autoTrigger = new BooleanSetting("Auto Trigger", "Automatically trigger tickbase", false);
    private final NumberSetting<Integer> autoInterval = new NumberSetting<>("Auto Interval", "Interval between auto triggers (ticks)", 40, 20, 200);

    private int accumulatedTicks = 0;
    private boolean isAccumulating = false;
    private boolean isReleasing = false;
    private int autoTriggerTimer = 0;
    private int releaseTicks = 0;

    public TickBaseModule() {
        super(
                "TickBase",
                "Tick Base",
                "Exploits Minecraft's tickspeed to speed up the game while appearing like lag to server.",
                ModuleCategory.UTILITY
        );

        addSetting(ticksToAccumulate);
        addSetting(freezeSpeed);
        addSetting(releaseSpeed);
        addSetting(autoTrigger);
        addSetting(autoInterval);
    }

    @Override
    protected void onEnable() {
        reset();
    }

    @Override
    protected void onDisable() {
        reset();
        TimerUtility.resetTimer();
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(mc.player == null || mc.world == null) return;

        if(autoTrigger.getValue()) {
            autoTriggerTimer++;
            if(autoTriggerTimer >= autoInterval.getValue() && !isAccumulating && !isReleasing) {
                startAccumulation();
                autoTriggerTimer = 0;
            }
        }

        if(isAccumulating) {
            handleAccumulation();
        } else if(isReleasing) {
            handleRelease();
        }
    };

    private void startAccumulation() {
        isAccumulating = true;
        accumulatedTicks = 0;
        TimerUtility.setTimerSpeed(freezeSpeed.getValue());
    }

    private void handleAccumulation() {
        accumulatedTicks++;

        if(accumulatedTicks >= ticksToAccumulate.getValue()) {
            isAccumulating = false;
            isReleasing = true;
            releaseTicks = 0;
            TimerUtility.setTimerSpeed(releaseSpeed.getValue());
        }
    }

    private void handleRelease() {
        releaseTicks++;

        if(releaseTicks >= accumulatedTicks) {
            isReleasing = false;
            TimerUtility.resetTimer();
            accumulatedTicks = 0;
            releaseTicks = 0;
        }
    }

    private void reset() {
        isAccumulating = false;
        isReleasing = false;
        accumulatedTicks = 0;
        releaseTicks = 0;
        autoTriggerTimer = 0;
    }

}
