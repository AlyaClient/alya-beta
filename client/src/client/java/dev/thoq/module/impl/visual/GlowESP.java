package dev.thoq.module.impl.visual;

import dev.thoq.module.Module;
import net.minecraft.entity.Entity;

public class GlowESP extends Module {

    public GlowESP() {
        super("glowesp", "glows entities");
    }

    @Override
    protected void onTick() {
        if (!isEnabled() || mc.world == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity != mc.player) {
                entity.setGlowing(true);
            }
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if (mc.world != null) {
            for (Entity entity : mc.world.getEntities()) {
                entity.setGlowing(false);
            }
        }
    }
}
