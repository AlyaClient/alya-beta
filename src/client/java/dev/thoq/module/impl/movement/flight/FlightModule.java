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

package dev.thoq.module.impl.movement.flight;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.flight.vanilla.CreativeFlight;
import dev.thoq.module.impl.movement.flight.vanilla.NormalFlight;
import dev.thoq.module.impl.movement.flight.verus.VerusDamageFly;
import dev.thoq.module.impl.movement.flight.verus.VerusGlideFly;
import dev.thoq.module.impl.movement.flight.verus.VerusPacketFlight;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class FlightModule extends Module {

    private boolean wasSprinting = false;

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);
        this.addSubmodules(new NormalFlight(this), new CreativeFlight(this), new VerusPacketFlight(this), new VerusDamageFly(this), new VerusGlideFly(this));
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) wasSprinting = mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        if(mc.player == null) return;

        mc.player.setSprinting(wasSprinting);
        mc.player.bodyYaw = 0f;

        if(mc.player.getAbilities().flying && !mc.player.isCreative()) {
            mc.player.getAbilities().flying = false;
        }

        mc.player.getAbilities().setFlySpeed(0.05f);
    }
}
