/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.utility.antivoid;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.utility.antivoid.position.PositionAntiVoid;

public final class AntiVoidModule extends Module {
    public AntiVoidModule() {
        super("AntiVoid", "Anti-Void", "Prevents you from falling in the void", ModuleCategory.UTILITY);

        this.addSubmodules(
                new PositionAntiVoid(this)
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
