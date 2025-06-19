package dev.thoq.module.impl.player.sprint;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;

public class SprintModule extends Module {

    public SprintModule() {
        super("Sprint", "Makes player less american", ModuleCategory.PLAYER);
    }

    @Override
    protected void onTick() {
        if(isEnabled() && mc.player != null) {
            mc.player.setSprinting(mc.player.forwardSpeed > 0);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if(mc.player == null) return;
        mc.player.setSprinting(false);
    }

}
