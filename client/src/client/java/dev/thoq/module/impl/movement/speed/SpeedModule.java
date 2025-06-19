package dev.thoq.module.impl.movement.speed;

import dev.thoq.config.BooleanSetting;
import dev.thoq.config.ModeSetting;
import dev.thoq.config.Setting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.speed.normal.NormalSpeed;
import dev.thoq.module.impl.movement.speed.verus.VerusSpeed;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class SpeedModule extends Module {

    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average American", ModuleCategory.MOVEMENT);

        ModeSetting mode = new ModeSetting("Mode", "Speed mode", "Normal", "Normal", "Verus");
        Setting<Float> speed = new Setting<>("Speed", "Zoom speed multiplier", 1.5f, 0.1f, 10.0f);
        BooleanSetting bHop = new BooleanSetting("BHop", "Enable BHop?", true);
        BooleanSetting strafe = new BooleanSetting("Strafe", "Enable Strafe?", true);
        BooleanSetting verusDamageBoost = new BooleanSetting("Damage boost", "Boost speed when damaged", true);

        speed.setVisibilityCondition(() -> "Normal".equals(mode.getValue()));
        bHop.setVisibilityCondition(() -> "Normal".equals(mode.getValue()));
        strafe.setVisibilityCondition(() -> "Normal".equals(mode.getValue()));
        verusDamageBoost.setVisibilityCondition(() -> "Verus".equals(mode.getValue()));

        addSetting(mode);
        addSetting(speed);
        addSetting(bHop);
        addSetting(strafe);
        addSetting(verusDamageBoost);
    }

    @Override
    protected void onTick() {
        if(!isEnabled()) return;
        GameOptions options = mc.options;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
            case "Normal": {
                float speed = ((Setting<Float>) getSetting("speed")).getValue();
                boolean bHop = ((BooleanSetting) getSetting("BHop")).getValue();
                boolean strafe = ((BooleanSetting) getSetting("Strafe")).getValue();

                NormalSpeed.normalSpeed(mc, options, speed, bHop, strafe);
                break;
            }

            case "Verus": {
                boolean verusDamageBoost = ((BooleanSetting) getSetting("Damage boost")).getValue();

                VerusSpeed.verusSpeed(mc, options, verusDamageBoost);
                break;
            }
        }
    }

    @Override
    protected void onEnable() {
        if(mc.player != null) wasSprinting = mc.player.isSprinting();
    }

    @Override
    protected void onDisable() {
        if(mc.player != null) mc.player.setSprinting(wasSprinting);
    }
}
