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

package dev.thoq.module.impl.movement;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.entity.Entity;

// todo: unbork (implement)
public class TargetStrafe extends Module {
    private final ModeSetting targetMode = new ModeSetting("Target", "Target types", "Players", "Players", "Passive", "Hostile", "All");
    private final NumberSetting<Float> range = new NumberSetting<>("Range", "Distance from target to strafe", 3.0f, 0.1f, 6.0f);
    private final NumberSetting<Float> speed = new NumberSetting<>("Speed", "Strafe speed", 0.28f, 0.1f, 1.0f);
    private final BooleanSetting adaptiveSpeed = new BooleanSetting("Adaptive Speed", "Adjust speed based on distance", true);
    private final BooleanSetting jumpStrafe = new BooleanSetting("Jump Strafe", "Allow strafing in air", false);

    private Entity currentTarget = null;
    private float strafeDirection = 1.0f;
    private int directionChangeTimer = 0;

    public TargetStrafe() {
        super("TargetStrafe", "Target Strafe", "Automatically move around targets", ModuleCategory.MOVEMENT);
        addSetting(targetMode);
        addSetting(range);
        addSetting(speed);
        addSetting(adaptiveSpeed);
        addSetting(jumpStrafe);
    }
}