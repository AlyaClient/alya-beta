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

package rip.tenacity.module;

import rip.tenacity.TenacityClient;
import rip.tenacity.config.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class SubModule {

    public final String name;
    protected final Module parent;
    protected final MinecraftClient mc;

    protected final List<Setting<?>> settings = new ArrayList<>();

    public SubModule(final String name, final Module parent) {
        this.name = name;
        this.parent = parent;
        this.mc = parent.mc;
    }

    public void onEnable() {
        TenacityClient.getEventBus().subscribe(this);
    }

    public void onDisable() {
        reset();
        TenacityClient.getEventBus().unsubscribe(this);
    }

    public void addSettings(final Setting<?>... settings) {
        for(final Setting<?> setting : settings) {
            setting.setVisibilityCondition(() -> this.parent.mode.getValue().equals(this.name));
            this.settings.add(setting);
        }
    }

    public void reset() {
    }
}
