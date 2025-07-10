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

package works.alya.module.impl.movement.speed;

import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.impl.movement.speed.blocksmc.BlocksMCSpeed;
import works.alya.module.impl.movement.speed.ncp.NCPSpeed;
import works.alya.module.impl.movement.speed.normal.NormalSpeed;
import works.alya.module.impl.movement.speed.spartan.SpartanSpeed;
import works.alya.module.impl.movement.speed.verus.VerusSpeed;
import works.alya.module.impl.movement.speed.vulcan.VulcanSpeed;
import works.alya.utilities.player.TimerUtility;

public class SpeedModule extends Module {

    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average American", ModuleCategory.MOVEMENT);
        this.addSubmodules(
                new NormalSpeed(this),
                new NCPSpeed(this),
                new VerusSpeed(this),
                new BlocksMCSpeed(this),
                new SpartanSpeed(this),
                new VulcanSpeed(this)
        );
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if(this.mc.player != null)
            this.wasSprinting = this.mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if(this.mc.player != null)
            this.mc.player.setSprinting(this.wasSprinting);
        TimerUtility.resetTimer();
    }
}