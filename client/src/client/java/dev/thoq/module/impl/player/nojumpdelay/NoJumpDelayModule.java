package dev.thoq.module.impl.player.nojumpdelay;

import dev.thoq.mixin.client.LivingEntityJumpAccessor;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class NoJumpDelayModule extends Module {
    public NoJumpDelayModule() {
        super("NoJumpDelay", "Makes player a bouncy ball", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        if(mc.player.isOnGround()) {
            LivingEntityJumpAccessor livingEntityJumpAccessor = (LivingEntityJumpAccessor) mc.player;
            livingEntityJumpAccessor.setJumpingCooldown(0);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
