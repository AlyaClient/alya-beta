/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.module.impl.visual;

import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
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
