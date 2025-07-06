/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.module.impl.movement.longjump.verus;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.player.MoveUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class VerusFireballLongJump extends SubModule {
    private static boolean hasThrown = false;
    private static int originalSlot = -1;

    public VerusFireballLongJump(final Module parent) {
        super("VerusFireball", parent);
    }

    private final IEventListener<TickEvent> onTick = event -> {
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
    };

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
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
