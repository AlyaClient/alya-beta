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

package works.alya.module.impl.movement.longjump;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.movement.longjump.verus.VerusFireballLongJump;
import works.alya.module.impl.movement.longjump.verus.VerusPacketLongjump;
import works.alya.utilities.player.MoveUtility;

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