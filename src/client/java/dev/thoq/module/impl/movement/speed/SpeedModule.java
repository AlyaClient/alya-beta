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

    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average American", ModuleCategory.MOVEMENT);
        this.addSubmodules(new NormalSpeed(this), new NCPSpeed(this), new VerusSpeed(this));
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if(this.mc.player != null) this.wasSprinting = this.mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if(this.mc.player != null) this.mc.player.setSprinting(this.wasSprinting);
        TimerUtility.resetTimer();
    }
}