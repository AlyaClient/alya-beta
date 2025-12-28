/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.visual;

import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public final class AntiInvisModule extends Module {
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
