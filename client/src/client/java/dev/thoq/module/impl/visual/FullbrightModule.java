package dev.thoq.module.impl.visual;

import dev.thoq.module.Module;

public class FullbrightModule extends Module {
    private double previousGamma;

    public FullbrightModule() {
        super("fullbright", "light mode for minecraft caves");
    }

    @Override
    protected void onTick() {
    }

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(1.0D);
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);
    }
}
