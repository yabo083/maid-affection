package com.github.touhoumaidaffection.network;

import com.github.touhoumaidaffection.client.KissClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KissMaidPayload {
    private final int maidEntityId;
    private final int playerEntityId;

    public KissMaidPayload(int maidEntityId, int playerEntityId) {
        this.maidEntityId = maidEntityId;
        this.playerEntityId = playerEntityId;
    }

    public int maidEntityId() {
        return maidEntityId;
    }

    public int playerEntityId() {
        return playerEntityId;
    }

    public static void encode(KissMaidPayload message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.maidEntityId);
        buf.writeVarInt(message.playerEntityId);
    }

    public static KissMaidPayload decode(FriendlyByteBuf buf) {
        return new KissMaidPayload(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(KissMaidPayload message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> KissClientHandler.handle(message)));
        context.setPacketHandled(true);
    }
}
