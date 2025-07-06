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

package rip.tenacity.module.impl.utility.disabler;

import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.utility.disabler.cubecraft.CubecraftDisabler;
import rip.tenacity.module.impl.utility.disabler.omnisprint.OmniSprintDisabler;
import rip.tenacity.module.impl.utility.disabler.spartan.SpartanDisabler;

public class DisablerModule extends Module {
    public DisablerModule() {
        super("Disabler", "Partially or fully disable some anticheats", ModuleCategory.UTILITY);

        this.addSubmodules(
                new SpartanDisabler(this),
                new OmniSprintDisabler(this),
                new CubecraftDisabler(this)
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
