package dev.thoq.module.impl.movement.flight.verus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class VerusFlight {
    private final VerusDamageFly verusDamageFly = new VerusDamageFly();
    private final VerusPacketFlight verusFlight = new VerusPacketFlight();
    private final VerusGlideFly verusGlideFly = new VerusGlideFly();

    public void verusFlight(
            MinecraftClient mc,
            GameOptions options,
            String verusMode,
            boolean clip
    ) {
        switch(verusMode) {
            case "FunnyPacket" -> {
                verusFlight.verusFlight(mc, options);
                verusFlight.sendVerusPackets(mc);
            }
            case "DamageFly" -> verusDamageFly.damageFly(mc, options);
            case "Glide" -> verusGlideFly.verusGlideFly(mc, clip);
        }
    }

    public void verusPacket(Packet<?> packet, CallbackInfo callbackInfo, String verusMode) {
        if(verusMode.equals("FunnyPacket")) {
            verusFlight.cancelPackets(packet, callbackInfo);
        }
    }

    public void reset() {
        verusDamageFly.reset();
        verusGlideFly.reset();
    }
}