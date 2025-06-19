package dev.thoq.module.impl.movement.flight.vanilla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;

public class CreativeFlight {
    public static void creativeFlight(MinecraftClient mc, GameOptions options, float speed, boolean verticalEnabled) {
        if(mc.player == null) return;

        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed * 0.05f);

        if(!verticalEnabled) {
            if(options.jumpKey.isPressed() || options.sneakKey.isPressed()) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0, vel.z);
            }
        }
    }
}
