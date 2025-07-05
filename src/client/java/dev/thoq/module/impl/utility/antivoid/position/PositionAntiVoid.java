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

package dev.thoq.module.impl.utility.antivoid.position;

import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.module.SubModule;
import dev.thoq.utilities.misc.ChatUtility;
import dev.thoq.utilities.player.PlayerUtility;

/**
 * @author slqnt
 * @since 0.1
 */
public class PositionAntiVoid extends SubModule {
    private final PlayerUtility playerUtility = new PlayerUtility();
    private final NumberSetting<Integer> distance = new NumberSetting<>("Distance", "Distance to check for void", 4, 1, 10);

    public PositionAntiVoid(Module parent) {
        super("Position", parent);

        this.addSettings(distance);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || !event.isPre()) return;

        boolean isOverVoid = playerUtility.isOverVoid();
        boolean isFalling = mc.player.fallDistance > distance.getValue();

        ChatUtility.sendDebug("Is over void:" + isOverVoid);
        ChatUtility.sendDebug("Is falling:" + isFalling);
        if(isFalling && isOverVoid) {
            event.setY(event.getY() + mc.player.fallDistance);
        }
    };

}
