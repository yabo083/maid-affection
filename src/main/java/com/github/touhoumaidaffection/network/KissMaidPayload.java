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
    private final boolean carriedKiss;

    public KissMaidPayload(int maidEntityId, int playerEntityId, boolean carriedKiss) {
        this.maidEntityId = maidEntityId;
        this.playerEntityId = playerEntityId;
        this.carriedKiss = carriedKiss;
    }

    public int maidEntityId() {
        return maidEntityId;
    }

    public int playerEntityId() {
        return playerEntityId;
    }

    public boolean carriedKiss() {
        return carriedKiss;
    }

    public static void encode(KissMaidPayload message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.maidEntityId);
        buf.writeVarInt(message.playerEntityId);
        buf.writeBoolean(message.carriedKiss);
    }

    public static KissMaidPayload decode(FriendlyByteBuf buf) {
        return new KissMaidPayload(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
    }

    public static void handle(KissMaidPayload message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> KissClientHandler.handle(message)));
        context.setPacketHandled(true);
    }
}
