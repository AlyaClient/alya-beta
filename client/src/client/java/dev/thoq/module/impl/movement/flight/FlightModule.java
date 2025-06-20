package dev.thoq.module.impl.movement.flight;

import dev.thoq.config.setting.impl.NumberSetting;
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

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);

        ModeSetting modeSetting = new ModeSetting("Mode", "Flight mode type", "Normal", "Normal", "Creative", "Verus");
        BooleanSetting verticalSetting = new BooleanSetting("Vertical", "Enable Vertical movement", true);
        NumberSetting<Float> speedSetting = new NumberSetting<>("Speed", "Flight speed multiplier", 1.5f, 0.1f, 10.0f);

        speedSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue()));
        verticalSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue()));

        addSetting(modeSetting);
        addSetting(speedSetting);
        addSetting(verticalSetting);
    }

    @Override
    protected void onPreTick() {
        if(!isEnabled() || mc.player == null) return;

        GameOptions options = mc.options;
        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        float speed = ((NumberSetting<Float>) getSetting("Speed")).getValue();
        boolean verticalEnabled = ((BooleanSetting) getSetting("Vertical")).getValue();

        switch(mode) {
            case "Normal":
                NormalFlight.normalFlight(mc, options, speed, verticalEnabled);
                break;
            case "Creative":
                CreativeFlight.creativeFlight(mc, options, speed, verticalEnabled);
                break;
            case "Verus":
                VerusFlight.verusFlight(mc, options);
                break;
        }
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
            mc.player.getAbilities().setFlySpeed(0.05f);
        }
    }
}
