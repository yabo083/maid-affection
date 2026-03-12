package com.github.touhoumaidaffection.network;

import com.github.touhoumaidaffection.handler.KissMaidHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KissCarryRequestMessage {
    public static void encode(KissCarryRequestMessage message, FriendlyByteBuf buf) {
    }

    public static KissCarryRequestMessage decode(FriendlyByteBuf buf) {
        return new KissCarryRequestMessage();
    }

    public static void handle(KissCarryRequestMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                KissMaidHandler.tryKissCarriedMaid(sender);
            }
        });
        context.setPacketHandled(true);
    }
}
