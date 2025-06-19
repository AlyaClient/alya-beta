package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.config.ModeSetting;
import dev.thoq.module.impl.movement.longjump.verus.fireball.VerusFireballLongJump;
import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;

public class VerusLongJump {
    public static void verusLongJump(MinecraftClient mc, ModeSetting verusMode) {
        if(mc.player == null) return;

        switch(verusMode.getValue()) {
            case "Fireball": {
                if(!VerusFireballLongJump.hasThrown()) VerusFireballLongJump.verusFireballLongJump(mc);
                break;
            }

            case "Packet": {
                if(mc.player.isOnGround() && MovementUtility.isMoving()) {
                    mc.player.jump();
                    MovementUtility.setSpeed(0.5f, false);
                }

                break;
            }
        }
    }
}
