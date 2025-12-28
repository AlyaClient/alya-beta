/*
 * Copyright (c) Alya Beta 2024 - 2025
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes it will be useful.
 *
 * Alya (and subsequently, all its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.mixin.client.network;

import works.alya.AlyaClient;
import works.alya.event.impl.PacketReceiveEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onPacketReceived(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        PacketReceiveEvent event = new PacketReceiveEvent(packet);
        AlyaClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("TAIL"))
    private void onPacketReceivedPost(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        PacketReceiveEvent event = new PacketReceiveEvent(packet);
        event.setPacket(packet);
        AlyaClient.getEventBus().dispatch(new PacketReceiveEvent(packet));
    }
}