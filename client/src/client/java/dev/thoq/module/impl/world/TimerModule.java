package dev.thoq.module.impl.world;

import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.player.TimerUtility;

public class TimerModule extends Module {
    private final NumberSetting<Float> timerSpeed = new NumberSetting<>("Speed", "Game speed multiplier", 1.0f, 0.1f, 10.0f);

    public TimerModule() {
        super("Timer", "Allows you to change the game speed", ModuleCategory.WORLD);

        addSetting(timerSpeed);
    }

    @Override
    protected void onTick() {
        if (timerSpeed.getValue() != TimerUtility.getTimerSpeed()) {
            TimerUtility.setTimerSpeed(timerSpeed.getValue());
        }
    }

    @Override
    protected void onDisable() {
        TimerUtility.resetTimer();
    }
}
