package dev.thoq.module.impl.visual;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.entity.Entity;

public class GlowESP extends Module {

    public GlowESP() {
        super("GlowESP", "Glows entities", ModuleCategory.VISUAL);
    }

    @Override
    protected void onPreTick() {
        if(!isEnabled() || mc.world == null) return;

        for(Entity entity : mc.world.getEntities()) {
            if(entity != mc.player) {
                entity.setGlowing(true);
            }
        }
    }

    @Override
    protected void onDisable() {
        if(mc.world == null) return;

        for(Entity entity : mc.world.getEntities()) {
            entity.setGlowing(false);
        }
    }
}