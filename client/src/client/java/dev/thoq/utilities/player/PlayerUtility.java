package dev.thoq.utilities.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PlayerUtility {
    private static float serverPitch;

    public static void setServerPitch(float pitch) {
        serverPitch = pitch;
    }

    public static float getServerPitch() {
        return serverPitch;
    }

    public static void applyDamage(MinecraftClient mc, int height) {
        if(mc.player == null || mc.getNetworkHandler() == null) return;

        double x = mc.player.getX();
        double baseY = mc.player.getY();
        double z = mc.player.getZ();
        float pitch = mc.player.getPitch();
        float yaw = mc.player.getYaw();
        boolean onGround = false;
        boolean horizontalCollision = mc.player.horizontalCollision;

        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, baseY + height, z, yaw, pitch, onGround, horizontalCollision));
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(x, baseY, z, yaw, pitch, false, horizontalCollision));
    }
}
