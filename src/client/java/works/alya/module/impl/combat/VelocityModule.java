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

package works.alya.module.impl.combat;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.PacketReceiveEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.misc.ChatUtility;

@SuppressWarnings({"rawtypes", "unchecked", "FieldCanBeLocal"})
public class VelocityModule extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Different modes", "Standard", "Standard", "Jump Reset", "Hurt Time");
    private final NumberSetting horizontalVelocity = new NumberSetting("Horizontal", "X + Z velocity multiplier", 1.0f, 0f, 1.0f);
    private final NumberSetting verticalVelocity = new NumberSetting("Vertical", "Y velocity multiplier", 1.0f, 0f, 1.0f);

    public VelocityModule() {
        super("Velocity", "Makes you variably American", ModuleCategory.COMBAT);

        addSetting(mode);
        addSetting(horizontalVelocity.setVisibilityCondition(() -> "Standard".equals(mode.getValue())));
        addSetting(verticalVelocity.setVisibilityCondition(() -> "Standard".equals(mode.getValue())));
    }

    @SuppressWarnings("unused")
    private final IEventListener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        if(!isEnabled() || mc.player == null || !event.isPre()) return;

        String mode = ((ModeSetting) getSetting("Mode")).getValue();

        if(event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
            switch(mode) {
                case "Standard": {
                    float xz = ((NumberSetting<Float>) getSetting("Horizontal")).getValue();
                    float y = ((NumberSetting<Float>) getSetting("Vertical")).getValue();

                    ChatUtility.sendDebug("player velocity >modified<");
                    mc.player.setVelocity(
                            mc.player.getVelocity().x * xz,
                            mc.player.getVelocity().y * y,
                            mc.player.getVelocity().z * xz
                    );

                    event.cancel();

                    break;
                }

                case "Jump Reset": {
                    if(mc.player.isOnGround()) {
                        mc.player.jump();
                    }

                    break;
                }

                case "Hurt Time": {
                    double multiplierXZ = 0.6 / mc.player.hurtTime;
                    double multiplierY = 0.9 / mc.player.hurtTime;

                    if(mc.player.hurtTime > 0) {
                        ChatUtility.sendDebug("player velocity >modified<");
                        ChatUtility.sendDebug(String.format("[multiplierXZ: %f], [multiplierY: %f]", multiplierXZ, multiplierY));
                        mc.player.setVelocity(
                                mc.player.getVelocity().y * multiplierXZ,
                                mc.player.getVelocity().y * multiplierY,
                                mc.player.getVelocity().z * multiplierXZ
                        );
                    }

                    break;
                }
            }
        }
    };
}
