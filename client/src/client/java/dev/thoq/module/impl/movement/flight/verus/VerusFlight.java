package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class VerusFlight {
    public static void verusFlight(MinecraftClient mc) {
        if(mc.player == null) return;
        if(mc.getNetworkHandler() == null) return;

        double xVelocity = mc.player.getVelocity().x;
        double zVelocity = mc.player.getVelocity().z;

        BlockPos pos = mc.player.getBlockPos().down();
        Direction facing = Direction.UP;
        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(
                Hand.MAIN_HAND,
                new BlockHitResult(
                        Vec3d.ofCenter(pos.up()),
                        facing,
                        pos,
                        false
                ),
                0
        );

        PlayerMoveC2SPacket rotationPacket = new PlayerMoveC2SPacket.LookAndOnGround(
                mc.player.getYaw(),
                90f,
                true,
                true
        );

        mc.getNetworkHandler().sendPacket(rotationPacket);
        mc.getNetworkHandler().sendPacket(packet);
        mc.player.setVelocity(xVelocity, 0, zVelocity);

        MovementUtility.setSpeed(0.3);
    }
}
