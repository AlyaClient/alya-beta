/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.module.impl.movement.flight;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.movement.flight.anarchy.AnarchyFlight;
import works.alya.module.impl.movement.flight.ncp.NCPFlight;
import works.alya.module.impl.movement.flight.vanilla.CreativeFlight;
import works.alya.module.impl.movement.flight.vanilla.NormalFlight;
import works.alya.module.impl.movement.flight.verus.VerusDamageFly;
import works.alya.module.impl.movement.flight.verus.VerusGlideFly;
import works.alya.module.impl.movement.flight.verus.VerusPacketFlight;

public class FlightModule extends Module {

    private boolean wasSprinting = false;

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);
        this.addSubmodules(
                new NormalFlight(this),
                new CreativeFlight(this),
                new VerusPacketFlight(this),
                new VerusDamageFly(this),
                new VerusGlideFly(this),
                new NCPFlight(this),
                new AnarchyFlight(this)
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
