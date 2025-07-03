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

package dev.thoq.module.impl.movement.longjump;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.longjump.verus.VerusFireballLongJump;
import dev.thoq.module.impl.movement.longjump.verus.VerusPacketLongjump;
import dev.thoq.utilities.player.MoveUtility;

public class LongJumpModule extends Module {

    public LongJumpModule() {
        super("LongJump", "Long Jump", "Makes you jump further", ModuleCategory.MOVEMENT);
        this.addSubmodules(new VerusFireballLongJump(this), new VerusPacketLongjump(this));
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        MoveUtility.setMotionY(0);
    }
}