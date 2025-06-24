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

package dev.thoq.module.impl.movement.longjump;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.longjump.verus.VerusLongJump;
import dev.thoq.module.impl.movement.longjump.verus.VerusFireballLongJump;
import dev.thoq.module.impl.movement.longjump.verus.VerusPacketLongjump;
import dev.thoq.utilities.player.MovementUtility;

public class LongJumpModule extends Module {
    public LongJumpModule() {
        super("LongJump", "Makes you jump further", ModuleCategory.MOVEMENT);

        ModeSetting mode = new ModeSetting("Mode", "Speed mode", "Verus", "Verus");
        ModeSetting verusMode = new ModeSetting("Kind", "Kind of LongJump to use", "Fireball", "Fireball", "Packet");

        verusMode.setVisibilityCondition(() -> "Verus".equals(((ModeSetting) getSetting("Mode")).getValue()));

        addSetting(mode);
        addSetting(verusMode);
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
            case "Verus": {
                String kind = ((ModeSetting) getSetting("Kind")).getValue();
                VerusLongJump.verusLongJump(mc, (ModeSetting) getSetting("Kind"));
                
                if ("Fireball".equals(kind) && isEnabled()) {
                    if(VerusFireballLongJump.hasThrown()) {
                        super.toggle();
                    }
                }

                if ("Packet".equals(kind) && isEnabled()) {
                    if(VerusPacketLongjump.hasJumped()) {
                        super.toggle();
                    }
                }

                break;
            }
        }
    }

    @Override
    protected void onEnable() {
        VerusLongJump.reset();
    }

    @Override
    protected void onDisable() {
        VerusLongJump.reset();
        MovementUtility.setMotionY(0);
    }
}