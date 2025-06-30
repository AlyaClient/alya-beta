/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.movement.flight.verus;

import dev.thoq.event.impl.MotionEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class VerusFlight {
    private final VerusDamageFly verusDamageFly = new VerusDamageFly();
    private final VerusPacketFlight verusFlight = new VerusPacketFlight();
    private final VerusGlideFly verusGlideFly = new VerusGlideFly();

    public void verusFlight(
            MinecraftClient mc,
            GameOptions options,
            String verusMode,
            boolean clip,
            MotionEvent event
    ) {
        switch(verusMode) {
            case "Infinite" -> {
                verusFlight.verusFlight(mc, options);
                verusFlight.sendVerusPackets(mc, event);
            }
            case "Damage" -> verusDamageFly.damageFly(mc, options);
            case "Glide" -> verusGlideFly.verusGlideFly(mc, clip);
        }
    }

    public void reset() {
        verusDamageFly.reset();
        verusGlideFly.reset();
    }
}