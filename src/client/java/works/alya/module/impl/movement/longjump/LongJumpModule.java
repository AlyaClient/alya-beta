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

package works.alya.module.impl.movement.longjump;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.movement.longjump.verus.VerusFireballLongJump;
import works.alya.module.impl.movement.longjump.verus.VerusPacketLongjump;
import works.alya.utilities.player.MoveUtility;

public final class LongJumpModule extends Module {

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