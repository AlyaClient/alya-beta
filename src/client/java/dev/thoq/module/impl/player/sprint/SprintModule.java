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

package dev.thoq.module.impl.player.sprint;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class SprintModule extends Module {

    public SprintModule() {
        super("Sprint", "Makes player less american", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if(isEnabled() && mc.player != null) {
            mc.player.setSprinting(mc.player.forwardSpeed > 0);
        }
    }

    @Override
    protected void onDisable() {
        if(mc.player == null) return;
        mc.player.setSprinting(false);
    }

}
