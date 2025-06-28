/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.movement.longjump.verus;

import dev.thoq.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class VerusFireballLongJump {
    private static boolean hasThrown = false;
    private static int originalSlot = -1;

    public void verusFireballLongJump(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        int fireballSlot = findFireballSlot(mc);
        if (fireballSlot == -1) return;

        if (!hasThrown) {
            originalSlot = mc.player.getInventory().getSelectedSlot();

            if (fireballSlot != originalSlot) {
                mc.player.getInventory().setSelectedSlot(fireballSlot);
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(fireballSlot));
            }

            mc.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), 90.0f, mc.player.isOnGround(), false)
            );

            mc.getNetworkHandler().sendPacket(
                    new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, mc.player.getYaw(), mc.player.getPitch())
            );

            MoveUtility.setSpeed(4f, true);

            hasThrown = true;
        }
    }

    private int findFireballSlot(MinecraftClient mc) {
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.FIRE_CHARGE)
                return i;
        }

        return -1;
    }

    public void reset() {
        hasThrown = false;
        originalSlot = -1;
    }

    public boolean hasThrown() {
        return hasThrown;
    }

}
