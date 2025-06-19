package dev.thoq.module.impl.movement.flight;

import dev.thoq.module.Module;
import dev.thoq.config.SliderSetting;
import dev.thoq.config.BooleanSetting;
import dev.thoq.config.ModeSetting;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.movement.flight.vanilla.CreativeFlight;
import dev.thoq.module.impl.movement.flight.vanilla.NormalFlight;
import dev.thoq.module.impl.movement.flight.verus.VerusFlight;
import dev.thoq.utilities.player.MovementUtility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@SuppressWarnings("unchecked")
public class FlightModule extends Module {
    private boolean wasSprinting = false;

    public FlightModule() {
        super("Flight", "Become airplane", ModuleCategory.MOVEMENT);

        ModeSetting modeSetting = new ModeSetting("Mode", "Flight mode type", "Normal", "Normal", "Creative", "Verus");
        BooleanSetting verticalSetting = new BooleanSetting("vertical", "Enable vertical movement", true);
        SliderSetting<Float> speedSetting = new SliderSetting<>("speed", "Flight speed multiplier", 1.5f, 0.1f, 10.0f);

        speedSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue()));
        verticalSetting.setVisibilityCondition(() -> "Normal".equals(modeSetting.getValue()));

        addSetting(modeSetting);
        addSetting(speedSetting);
        addSetting(verticalSetting);
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        GameOptions options = mc.options;
        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        float speed = ((SliderSetting<Float>) getSetting("speed")).getValue();
        boolean verticalEnabled = ((BooleanSetting) getSetting("vertical")).getValue();

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
        if(mc.player != null) {
            mc.player.setSprinting(wasSprinting);

            if(mc.player.getAbilities().flying && !mc.player.isCreative()) {
                mc.player.getAbilities().flying = false;
                mc.player.getAbilities().setFlySpeed(0.05f);
            }
        }
    }
}
