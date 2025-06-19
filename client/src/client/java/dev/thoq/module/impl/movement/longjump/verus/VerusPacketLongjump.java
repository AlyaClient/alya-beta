package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.utilities.player.MovementUtility;
import dev.thoq.utilities.player.TimerUtility;
import net.minecraft.client.MinecraftClient;

public class VerusPacketLongjump {
    static boolean jumped = false;

    public static void verusPacketLongjump(MinecraftClient mc) {
        if(mc.player == null) return;

        if(mc.player.isOnGround() && MovementUtility.isMoving()) {
            double timerSpeed = 0.1f;
            TimerUtility.setTimerSpeed(timerSpeed);

            MovementUtility.setMotionY(0.55);
            MovementUtility.setSpeed(1.2f, true);

            new Thread(() -> {
                try {
                    Thread.sleep(Math.round(150 / timerSpeed));
                } catch(InterruptedException ignored) {
                }

                jumped = true;
            }).start();
        }
    }

    public static void reset() {
        jumped = false;
    }

    public static boolean hasJumped() {
        return jumped;
    }
}
