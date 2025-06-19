package dev.thoq.module.impl.player.nofall.vanilla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class VanillaNoFall {
    public static void vanillaNoFall(MinecraftClient mc) {
        if(mc.player == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        PlayerMoveC2SPacket playerMoveC2SPacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false);

        mc.player.networkHandler.sendPacket(playerMoveC2SPacket);
        mc.player.fallDistance = 0;
    }
}
