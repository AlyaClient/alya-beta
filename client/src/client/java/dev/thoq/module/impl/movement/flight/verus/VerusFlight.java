package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class VerusFlight {
    public static void verusFlight(MinecraftClient mc, GameOptions options) {
        if(mc.player == null) return;
        if(mc.getNetworkHandler() == null) return;

        mc.player.setPitch(90f);

        Vec3d playerPos = mc.player.getPos().add(0, -1, 0);
        BlockPos blockPos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        BlockHitResult hitResult = new BlockHitResult(playerPos, Direction.UP, blockPos, false);
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0));

        MovementUtility.setMotionY(0);

        if(options.forwardKey.isPressed()) {
            MovementUtility.setSpeed(0.33);
        } else {
            MovementUtility.setSpeed(0);
        }
    }
}
