package dev.thoq.module;

import dev.thoq.RyeClient;
import dev.thoq.config.setting.Setting;
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
        RyeClient.getEventBus().subscribe(this);
    }

    public void onDisable() {
        RyeClient.getEventBus().unsubscribe(this);
    }

    public void addSettings(final Setting<?>... settings) {
        for(final Setting<?> setting : settings) {
            setting.setVisibilityCondition(() -> this.parent.mode.getValue().equals(this.name));
            this.settings.add(setting);
        }
    }

}
