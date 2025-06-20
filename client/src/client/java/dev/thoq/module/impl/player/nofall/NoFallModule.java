package dev.thoq.module.impl.player.nofall;

import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.player.nofall.vanilla.VanillaNoFall;
import dev.thoq.module.impl.player.nofall.verus.VerusNoFall;

public class NoFallModule extends Module {
    public NoFallModule() {
        super("NoFall", "Prevents fall damage", ModuleCategory.PLAYER);

        ModeSetting mode = new ModeSetting("Mode", "NoFall mode", "Vanilla", "Vanilla", "Verus");

        addSetting(mode);
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.player == null) return;

        switch(((ModeSetting) getSetting("Mode")).getValue()) {
            case "Vanilla": {
                VanillaNoFall.vanillaNoFall(mc);
                break;
            }

            case "Verus": {
                VerusNoFall.verusNoFall(mc);
                break;
            }
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}