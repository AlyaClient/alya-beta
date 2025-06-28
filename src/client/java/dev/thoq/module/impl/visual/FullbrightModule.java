/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.visual;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class FullbrightModule extends Module {
    private double previousGamma;

    public FullbrightModule() {
        super("FullBright", "Light mode for minecraft caves", ModuleCategory.VISUAL);
    }

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(1.0D);
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);
    }
}
