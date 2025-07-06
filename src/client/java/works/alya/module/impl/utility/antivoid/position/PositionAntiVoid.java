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

package works.alya.module.impl.utility.antivoid.position;

import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.misc.ChatUtility;
import works.alya.utilities.player.PlayerUtility;

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
