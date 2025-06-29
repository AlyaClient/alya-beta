/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;

public class VerusLongJump {
    private final VerusFireballLongJump fireballLongJump = new VerusFireballLongJump();
    private final VerusPacketLongjump packetLongJump = new VerusPacketLongjump();

    public void verusLongJump(MinecraftClient mc, ModeSetting verusMode) {
        if(mc.player == null) return;

        switch(verusMode.getValue()) {
            case "Fireball": {
                if(!fireballLongJump.hasThrown())
                    fireballLongJump.verusFireballLongJump(mc);
                break;
            }

            case "Packet": {
                if(!packetLongJump.hasJumped())
                    packetLongJump.verusPacketLongjump(mc);
                TimerUtility.resetTimer();
                break;
            }
        }
    }

    public void reset() {
        fireballLongJump.reset();
        packetLongJump.reset();
    }
}
