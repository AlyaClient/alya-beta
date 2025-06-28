/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.movement.flight;

import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.MotionEvent;
import dev.thoq.module.Module;
import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.flight.vanilla.CreativeFlight;
import dev.thoq.module.impl.movement.flight.vanilla.NormalFlight;
import dev.thoq.module.impl.movement.flight.verus.VerusFlight;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class FlightModule extends Module {
    private boolean wasSprinting = false;
    private final VerusFlight verusFlight = new VerusFlight();
    private final CreativeFlight creativeFlight = new CreativeFlight();
    private final NormalFlight normalFlight = new NormalFlight();

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);

        ModeSetting modeSetting = new ModeSetting("Mode", "Flight mode type", "Normal", "Normal", "Creative", "Verus");
        ModeSetting verusModeSetting = new ModeSetting("Verus Mode", "Type", "Infinite", "Infinite", "Damage", "Glide");
        BooleanSetting preventVanillaKick = new BooleanSetting("Anti-Kick", "Prevent Vanilla kick when flying", true);
        BooleanSetting verticalSetting = new BooleanSetting("Vertical", "Enable Vertical movement", true);
        BooleanSetting verusGlideClip = new BooleanSetting("Clip", "Clip up every 4s (80 ticks)", true);
        NumberSetting<Float> speedSetting = new NumberSetting<>("Speed", "Flight speed multiplier", 1.5f, 0.1f, 10.0f);

        addSetting(modeSetting);
        addSetting(preventVanillaKick.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue())));
        addSetting(speedSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue())));
        addSetting(verticalSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue())));
        addSetting(verusModeSetting.setVisibilityCondition(() -> "Verus".equals(modeSetting.getValue())));
        addSetting(verusGlideClip.setVisibilityCondition(() -> "Glide".equals(verusModeSetting.getValue())));
    }

    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(!isEnabled() || mc.player == null) return;

        GameOptions options = mc.options;
        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        float speed = ((NumberSetting<Float>) getSetting("Speed")).getValue();
        boolean verticalEnabled = ((BooleanSetting) getSetting("Vertical")).getValue();
        boolean preventVanillaKick = ((BooleanSetting) getSetting("Anti-Kick")).getValue();

        switch(mode) {
            case "Normal": {
                normalFlight.normalFlight(mc, options, speed, verticalEnabled, preventVanillaKick);

                break;
            }

            case "Creative": {
                creativeFlight.creativeFlight(mc, options, speed, verticalEnabled);

                break;
            }

            case "Verus": {
                String verusMode = ((ModeSetting) getSetting("Verus Mode")).getValue();
                boolean clip = ((BooleanSetting) getSetting("Clip")).getValue();

                verusFlight.verusFlight(mc, options, verusMode, clip, event);

                break;
            }
        }
    };

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
            mc.player.getAbilities().setFlySpeed(0.05f);
        }

        verusFlight.reset();
    }
}
