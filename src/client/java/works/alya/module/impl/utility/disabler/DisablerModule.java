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

package works.alya.module.impl.utility.disabler;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.utility.disabler.cubecraft.CubecraftDisabler;
import works.alya.module.impl.utility.disabler.omnisprint.OmniSprintDisabler;
import works.alya.module.impl.utility.disabler.spartan.SpartanDisabler;

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
