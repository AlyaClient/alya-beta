/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.mixin.client;

import dev.thoq.RyeClient;
import dev.thoq.event.impl.MotionEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        boolean onGround = player.isOnGround();

        MotionEvent event = new MotionEvent(x, y, z, yaw, pitch, onGround);

        RyeClient.getEventBus().dispatch(event);

        // TODO: correctly cancel
        if(event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsPost(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        boolean onGround = player.isOnGround();

        MotionEvent event = new MotionEvent(x, y, z, yaw, pitch, onGround);

        event.setPost();

        RyeClient.getEventBus().dispatch(event);
    }
}
