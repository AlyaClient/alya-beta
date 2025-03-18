package dev.thoq.module.impl.player;

import dev.thoq.module.Module;

public class NoJumpDelayModule extends Module {
    public NoJumpDelayModule() {
        super("NoJumpDelay", "Makes player a bouncy ball");
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        if(mc.player.isOnGround()) {
            mc.player.setJumping(false);
            if(mc.options.jumpKey.isPressed()) {
                mc.player.jump();
            }
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {}
}
