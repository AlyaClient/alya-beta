package dev.thoq.module.impl.movement.speed.verus;

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class VerusSpeed {
    public static void verusSpeed(MinecraftClient mc, GameOptions options, boolean verusDamageBoost) {
        if(mc.player == null) return;

        if(options.jumpKey.isPressed())
            MovementUtility.setSpeed(0.3f, true);

        if(mc.player.isOnGround() && MovementUtility.isMoving())
            mc.player.jump();

        if(verusDamageBoost && mc.player.hurtTime > 0)
            MovementUtility.setSpeed(1.0f, true);

        MovementUtility.setSpeed(0.37f, true);
    }
}
