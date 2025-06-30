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

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.longjump.verus.VerusLongJump;
import dev.thoq.module.impl.movement.longjump.verus.VerusFireballLongJump;
import dev.thoq.module.impl.movement.longjump.verus.VerusPacketLongjump;
import dev.thoq.utilities.player.MoveUtility;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class LongJumpModule extends Module {
    private final VerusFireballLongJump verusFireballLongJump = new VerusFireballLongJump();
    private final VerusPacketLongjump verusPacketLongjump = new VerusPacketLongjump();
    private final VerusLongJump verusLongJump = new VerusLongJump();

    public LongJumpModule() {
        super("LongJump", "Makes you jump further", ModuleCategory.MOVEMENT);

        ModeSetting mode = new ModeSetting("Mode", "Speed mode", "Verus", "Verus");
        ModeSetting verusMode = new ModeSetting("Kind", "Kind of LongJump to use", "Fireball", "Fireball", "Packet");

        verusMode.setVisibilityCondition(() -> "Verus".equals(((ModeSetting) getSetting("Mode")).getValue()));

        addSetting(mode);
        addSetting(verusMode);
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
            case "Verus": {
                String kind = ((ModeSetting) getSetting("Kind")).getValue();
                verusLongJump.verusLongJump(mc, (ModeSetting) getSetting("Kind"));
                
                if ("Fireball".equals(kind) && isEnabled()) {
                    if(verusFireballLongJump.hasThrown()) {
                        super.toggle();
                    }
                }

                if ("Packet".equals(kind) && isEnabled()) {
                    if(verusPacketLongjump.hasJumped()) {
                        super.toggle();
                    }
                }

                break;
            }
        }
    };

    @Override
    protected void onEnable() {
        verusLongJump.reset();
    }

    @Override
    protected void onDisable() {
        verusPacketLongjump.reset();
        MoveUtility.setMotionY(0);
    }
}