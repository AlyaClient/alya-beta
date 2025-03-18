package dev.thoq.module.impl.movment;

import dev.thoq.module.Module;
import dev.thoq.config.Setting;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("unchecked")
public class FlightModule extends Module {
    private boolean wasSprinting = false;

    public FlightModule() {
        super("Flight", "Become airplane");
        addSetting(new Setting<>("speed", "Flight speed multiplier", 1.5f, 0.1f, 10.0f));
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        GameOptions options = mc.options;
        float speed = ((Setting<Float>) getSetting("speed")).getValue();

        mc.player.setVelocity(0, 0, 0);
        mc.player.setSprinting(false);

        double verticalSpeed = 0;
        if(options.jumpKey.isPressed()) {
            verticalSpeed = speed / 2; // Up
        } else if(options.sneakKey.isPressed()) {
            verticalSpeed = -speed / 2; // Down
        }

        float forward = options.forwardKey.isPressed() ? 1.0f : options.backKey.isPressed() ? -1.0f : 0.0f;
        float sideways = options.leftKey.isPressed() ? 1.0f : options.rightKey.isPressed() ? -1.0f : 0.0f;

        float yaw = mc.player.getYaw();
        double radianYaw = Math.toRadians(yaw);

        double x = 0;
        double z = 0;

        if(forward != 0 || sideways != 0) {
            x -= forward * Math.sin(radianYaw);
            z += forward * Math.cos(radianYaw);

            x += sideways * Math.cos(radianYaw);
            z += sideways * Math.sin(radianYaw);

            double length = Math.sqrt(x * x + z * z);
            if(length > 0) {
                x = x / length * speed;
                z = z / length * speed;
            }
        }

        mc.player.setVelocity(new Vec3d(x, verticalSpeed, z));
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
