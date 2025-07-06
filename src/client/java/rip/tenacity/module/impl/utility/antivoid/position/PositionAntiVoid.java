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

package rip.tenacity.module.impl.utility.antivoid.position;

import rip.tenacity.config.setting.impl.NumberSetting;
import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.MotionEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.SubModule;
import rip.tenacity.utilities.misc.ChatUtility;
import rip.tenacity.utilities.player.PlayerUtility;

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
