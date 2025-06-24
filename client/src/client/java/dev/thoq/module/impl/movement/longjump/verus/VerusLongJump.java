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

package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;

public class VerusLongJump {
    public static void verusLongJump(MinecraftClient mc, ModeSetting verusMode) {
        if(mc.player == null) return;

        switch(verusMode.getValue()) {
            case "Fireball": {
                if(!VerusFireballLongJump.hasThrown())
                    VerusFireballLongJump.verusFireballLongJump(mc);
                break;
            }

            case "Packet": {
                if(!VerusPacketLongjump.hasJumped())
                    VerusPacketLongjump.verusPacketLongjump(mc);
                TimerUtility.resetTimer();
                break;
            }
        }
    }

    public static void reset() {
        VerusFireballLongJump.reset();
        VerusPacketLongjump.reset();
    }
}
