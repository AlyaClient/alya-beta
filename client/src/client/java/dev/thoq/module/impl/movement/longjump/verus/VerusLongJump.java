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
