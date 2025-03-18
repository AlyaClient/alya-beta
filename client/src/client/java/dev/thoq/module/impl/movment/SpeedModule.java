package dev.thoq.module.impl.movment;

import dev.thoq.config.Setting;
import dev.thoq.module.Module;
import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.option.GameOptions;

@SuppressWarnings("unchecked")
public class SpeedModule extends Module {

    private boolean wasSprinting = false;

    public SpeedModule() {
        super("Speed", "Become faster than the average american");
        addSetting(new Setting<>("Speed", "Zoom speed multiplier", 1.5f, 0.1f, 10.0f));
        addSetting(new Setting<>("BHop", "Enable BHop?", 1, 0, 1));
        addSetting(new Setting<>("Strafe", "Enable Strafe?", 0, 0, 1));
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        GameOptions options = mc.options;
        float speed = ((Setting<Float>) getSetting("speed")).getValue();
        float bHop = ((Setting<Float>) getSetting("BHop")).getValue();
        float strafe = ((Setting<Float>) getSetting("Strafe")).getValue();

        if(options.jumpKey.isPressed()) {
            speed = speed / 2;
        }

        if(bHop == 1 && mc.player.isOnGround() && MovementUtility.isMoving()) {
            mc.player.jump();
        }

        if(strafe == 1) {
            MovementUtility.setSpeed(speed, true);
        } else {
            MovementUtility.setSpeed(speed);
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
