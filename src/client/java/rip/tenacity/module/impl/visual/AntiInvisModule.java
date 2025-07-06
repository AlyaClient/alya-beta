/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.module.impl.visual;

import rip.tenacity.event.IEventListener;
import rip.tenacity.event.impl.TickEvent;
import rip.tenacity.module.Module;
import rip.tenacity.module.ModuleCategory;
import net.minecraft.entity.Entity;

public class AntiInvisModule extends Module {
    public AntiInvisModule() {
        super("AntiInvis", "Anti-Invis", "See things you're not supposed to, like your paren-", ModuleCategory.VISUAL);
    }

    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.world == null) return;

        Iterable<Entity> entities = mc.world.getEntities();

        for(Entity entity : entities) {
            if(entity.isInvisible())
                entity.setInvisible(false);
        }
    };

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
