package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;

public class VerusGlideFly {
    private static int timeRunning = 0;
    private static boolean messageSent = false;

    public void verusGlideFly(MinecraftClient mc, boolean clip) {
        if(mc.player == null) return;
        if(!messageSent) {
            ChatUtility.sendWarning("This fly does *NOT* new Verus, only old cracked versions!");
            messageSent = true;
        }

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();

        timeRunning++;

        if(clip && timeRunning >= 80) {
            mc.player.setPosition(posX, posY + 1, posZ);
            timeRunning = 0;
        }

        MovementUtility.setMotionY(-0.02);
        MovementUtility.setSpeed(0.1);
    }

    public void reset() {
        messageSent = false;
    }
}
