package dev.thoq.module.impl.client;

import dev.thoq.config.SliderSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.TimerUtility;

public class TimerModule extends Module {
    private final SliderSetting<Float> timerSpeed;
    private float previousTimerSpeed;

    public TimerModule() {
        super("Timer", "Allows you to change the game speed", ModuleCategory.CLIENT);
        
        timerSpeed = new SliderSetting<>("Speed", "Game speed multiplier", 1.0f, 0.1f, 10.0f);
        addSetting(timerSpeed);
    }

    @Override
    protected void onEnable() {
        previousTimerSpeed = TimerUtility.getTimerSpeed();
        TimerUtility.setTimerSpeed(timerSpeed.getValue());
    }

    @Override
    protected void onDisable() {
        TimerUtility.setTimerSpeed(1.0f);
    }

    @Override
    protected void onTick() {
        if (timerSpeed.getValue() != TimerUtility.getTimerSpeed()) {
            TimerUtility.setTimerSpeed(timerSpeed.getValue());
        }
    }
}