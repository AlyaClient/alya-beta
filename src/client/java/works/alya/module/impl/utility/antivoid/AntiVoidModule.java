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

package works.alya.module.impl.utility.antivoid;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.utility.antivoid.position.PositionAntiVoid;

public class AntiVoidModule extends Module {
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
