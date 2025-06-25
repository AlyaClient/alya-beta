/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.movement.speed;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.speed.ncp.NCPSpeed;
import dev.thoq.module.impl.movement.speed.normal.NormalSpeed;
import dev.thoq.module.impl.movement.speed.verus.VerusSpeed;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class SpeedModule extends Module {

    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average American", ModuleCategory.MOVEMENT);

        ModeSetting mode = new ModeSetting("Mode", "Speed mode", "Normal", "Normal", "Verus", "NCP");
        NumberSetting<Float> speed = new NumberSetting<>("Speed", "Zoom speed multiplier", 1.5f, 0.1f, 10.0f);
        BooleanSetting bHop = new BooleanSetting("BHop", "Enable BHop?", true);
        BooleanSetting strafe = new BooleanSetting("Strafe", "Enable Strafe?", true);
        BooleanSetting verusDamageBoost = new BooleanSetting("Damage boost", "Boost speed when damaged", true);
        BooleanSetting ncpDamageBoost = new BooleanSetting("Damage boost", "Boost speed when damaged", true);

        addSetting(mode);
        addSetting(speed.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(bHop.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(strafe.setVisibilityCondition(() -> "Normal".equals(mode.getValue())));
        addSetting(verusDamageBoost.setVisibilityCondition(() -> "Verus".equals(mode.getValue())));
        addSetting(ncpDamageBoost.setVisibilityCondition(() -> "NCP".equals(mode.getValue())));
    }

    @Override
    protected void onTick() {
        if(!isEnabled()) return;
        GameOptions options = mc.options;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
            case "Normal": {
                float speed = ((Setting<Float>) getSetting("speed")).getValue();
                boolean bHop = ((BooleanSetting) getSetting("BHop")).getValue();
                boolean strafe = ((BooleanSetting) getSetting("Strafe")).getValue();

                NormalSpeed.normalSpeed(mc, options, speed, bHop, strafe);
                break;
            }

            case "Verus": {
                boolean verusDamageBoost = ((BooleanSetting) getSetting("Damage boost")).getValue();

                VerusSpeed.verusSpeed(mc, options, verusDamageBoost);
                break;
            }

            case "NCP": {
                boolean ncpDamageBoost = ((BooleanSetting) getSetting("Damage boost")).getValue();

                NCPSpeed.ncpSpeed(mc, options, ncpDamageBoost);
                break;
            }
        }
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) wasSprinting = mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        if(mc.player != null) mc.player.setSprinting(wasSprinting);
        TimerUtility.resetTimer();
    }
}
