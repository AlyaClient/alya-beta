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

package rip.tenacity.module.impl.movement.flight;

import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import rip.tenacity.module.impl.movement.flight.vanilla.CreativeFlight;
import rip.tenacity.module.impl.movement.flight.vanilla.NormalFlight;
import rip.tenacity.module.impl.movement.flight.verus.VerusDamageFly;
import rip.tenacity.module.impl.movement.flight.verus.VerusGlideFly;
import rip.tenacity.module.impl.movement.flight.verus.VerusPacketFlight;

public class FlightModule extends Module {

    private boolean wasSprinting = false;

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);
        this.addSubmodules(
                new NormalFlight(this),
                new CreativeFlight(this),
                new VerusPacketFlight(this),
                new VerusDamageFly(this),
                new VerusGlideFly(this)
        );
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if(mc.player != null) wasSprinting = mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if(mc.player == null) return;

        mc.player.setSprinting(wasSprinting);
        mc.player.bodyYaw = 0f;

        if(mc.player.getAbilities().flying && !mc.player.isCreative()) {
            mc.player.getAbilities().flying = false;
        }

        mc.player.getAbilities().setFlySpeed(0.05f);
    }
}
