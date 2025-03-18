package dev.thoq.module.impl.player;

import dev.thoq.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("nofall", "Prevents fall damage");
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, true, false)
        );

        mc.player.fallDistance = 0;
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}