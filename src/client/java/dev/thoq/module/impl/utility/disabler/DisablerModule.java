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

package dev.thoq.module.impl.utility.disabler;

import dev.thoq.config.setting.impl.MultipleBooleanSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.event.impl.PacketSendEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.utility.disabler.cubecraft.CubecraftDisabler;
import dev.thoq.module.impl.utility.disabler.omnisprint.OmniSprintDisabler;

public class DisablerModule extends Module {
    private final OmniSprintDisabler omniSprintDisabler = new OmniSprintDisabler();
    private final CubecraftDisabler cubecraftDisabler = new CubecraftDisabler();

    private final MultipleBooleanSetting disabler = new MultipleBooleanSetting("Mode", "Disabler mode", "OmniSprint", "Cubecraft");

    public DisablerModule() {
        super("Disabler", "Partially or fully disable some anticheats", ModuleCategory.UTILITY);

        addSetting(disabler);
    }

    @SuppressWarnings({"unused"})
    private final IEventListener<PacketSendEvent> packetSendEvent = event -> {
        if(disabler.isEnabled("OmniSprint")) {
            omniSprintDisabler.omniSprintDisabler(event, mc);
        }
    };

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(disabler.isEnabled("Cubecraft")) {
            cubecraftDisabler.cubecraftDisabler(event, mc);
        }
    };
}