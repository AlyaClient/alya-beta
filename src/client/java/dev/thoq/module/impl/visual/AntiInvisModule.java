/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.visual;

import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.TickEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import net.minecraft.entity.Entity;

public class AntiInvisModule extends Module {
    public AntiInvisModule() {
        super("AntiInvis", "See things you're not supposed to, like your paren-", ModuleCategory.VISUAL);
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
