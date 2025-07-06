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

package rip.tenacity.module.impl.movement.longjump;

import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.movement.longjump.verus.VerusFireballLongJump;
import rip.tenacity.module.impl.movement.longjump.verus.VerusPacketLongjump;
import rip.tenacity.utilities.player.MoveUtility;

public class LongJumpModule extends Module {

    public LongJumpModule() {
        super("LongJump", "Long Jump", "Makes you jump further", ModuleCategory.MOVEMENT);
        this.addSubmodules(
                new VerusFireballLongJump(this),
                new VerusPacketLongjump(this)
        );
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        MoveUtility.setMotionY(0);
    }
}