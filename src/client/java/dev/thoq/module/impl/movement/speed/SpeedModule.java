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

package dev.thoq.module.impl.movement.speed;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.speed.ncp.NCPSpeed;
import dev.thoq.module.impl.movement.speed.normal.NormalSpeed;
import dev.thoq.module.impl.movement.speed.verus.VerusSpeed;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class SpeedModule extends Module {
    private final NormalSpeed normalSpeed = new NormalSpeed();
    private final VerusSpeed verusSpeed = new VerusSpeed();
    private final NCPSpeed ncpSpeed = new NCPSpeed();
    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average American", ModuleCategory.MOVEMENT);

        ModeSetting mode = new ModeSetting("Mode", "Speed mode", "Normal", "Normal", "Verus", "NCP");
        NumberSetting<Float> speed = new NumberSetting<>("Speed", "Zoom speed multiplier", 1.5f, 0.1f, 10.0f);
        BooleanSetting bHop = new BooleanSetting("BHop", "Enable BHop?", true);
        BooleanSetting strafe = new BooleanSetting("Strafe", "Enable Strafe?", true);
        BooleanSetting verusDamageBoost = new BooleanSetting("Damage boost", "Boost speed when damaged", true);

        addSetting(mode);
        addSetting(speed.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(bHop.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(strafe.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(verusDamageBoost.setVisibilityCondition(() -> "Verus".equals(mode.getValue())));
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || !event.isPre()) return;
        GameOptions options = mc.options;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        setPrefix(mode);

        switch(mode) {
            case "Normal": {
                float speed = ((Setting<Float>) getSetting("speed")).getValue();
                boolean bHop = ((BooleanSetting) getSetting("BHop")).getValue();
                boolean strafe = ((BooleanSetting) getSetting("Strafe")).getValue();

                normalSpeed.normalSpeed(mc, options, speed, bHop, strafe);

                break;
            }

            case "Verus": {
                boolean verusDamageBoost = ((BooleanSetting) getSetting("Damage boost")).getValue();

                verusSpeed.verusSpeed(mc, options, verusDamageBoost);

                break;
            }

            case "NCP": {
                ncpSpeed.ncpSpeed(mc, options);

                break;
            }
        }
    };

    @Override
    protected void onEnable() {
        if(mc.player != null) wasSprinting = mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        if(mc.player != null) mc.player.setSprinting(wasSprinting);
        TimerUtility.resetTimer();
        ncpSpeed.reset();
    }
}