package dev.thoq.module.impl.visual;

import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.entity.Entity;

public class AntiInvisModule extends Module {
    public AntiInvisModule() {
        super("AntiInvis", "See things you're not supposed to, like your paren-", ModuleCategory.VISUAL);
    }

    @Override
    protected void onTick() {
        if(!isEnabled() || mc.world == null) return;

        Iterable<Entity> entities = mc.world.getEntities();

        for(Entity entity : entities) {
            if(entity.isInvisible())
                entity.setInvisible(false);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if(mc.world == null) return;

        Iterable<Entity> entities = mc.world.getEntities();

        for(Entity entity : entities) {
            if(entity.isInvisible())
                entity.setInvisible(true);
        }
    }
}
