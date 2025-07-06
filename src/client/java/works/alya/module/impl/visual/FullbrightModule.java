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

import works.alya.module.Module;
import works.alya.module.ModuleCategory;

public class FullbrightModule extends Module {
    private double previousGamma;

    public FullbrightModule() {
        super("FullBright", "Full Bright", "Light mode for minecraft caves", ModuleCategory.VISUAL);
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
