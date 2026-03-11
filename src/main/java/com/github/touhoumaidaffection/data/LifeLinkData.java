package com.github.touhoumaidaffection.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record LifeLinkData(List<UUID> boundMaids, float currentEnergy, float maxEnergy) {

    public static final LifeLinkData EMPTY = new LifeLinkData(List.of(), 0f, 0f);

    public static final Codec<LifeLinkData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.listOf().fieldOf("bound_maids").forGetter(LifeLinkData::boundMaids),
                    Codec.FLOAT.fieldOf("current_energy").forGetter(LifeLinkData::currentEnergy),
                    Codec.FLOAT.fieldOf("max_energy").forGetter(LifeLinkData::maxEnergy)
            ).apply(instance, LifeLinkData::new)
    );

    public static final StreamCodec<ByteBuf, LifeLinkData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), LifeLinkData::boundMaids,
            ByteBufCodecs.FLOAT, LifeLinkData::currentEnergy,
            ByteBufCodecs.FLOAT, LifeLinkData::maxEnergy,
            LifeLinkData::new
    );

    public LifeLinkData withBoundMaid(UUID maidUuid) {
        List<UUID> newList = new ArrayList<>(boundMaids);
        if (!newList.contains(maidUuid)) {
            newList.add(maidUuid);
        }
        return new LifeLinkData(List.copyOf(newList), currentEnergy, maxEnergy);
    }

    public LifeLinkData withoutMaid(UUID maidUuid) {
        List<UUID> newList = new ArrayList<>(boundMaids);
        newList.remove(maidUuid);
        return new LifeLinkData(List.copyOf(newList), currentEnergy, maxEnergy);
    }

    public LifeLinkData withEnergy(float energy, float max) {
        return new LifeLinkData(boundMaids, Math.max(0, Math.min(energy, max)), max);
    }

    public LifeLinkData withDamage(float damage) {
        float newEnergy = Math.max(0, currentEnergy - damage);
        return new LifeLinkData(boundMaids, newEnergy, maxEnergy);
    }

    public boolean hasBoundMaids() {
        return !boundMaids.isEmpty();
    }

    public boolean isBound(UUID maidUuid) {
        return boundMaids.contains(maidUuid);
    }

    public boolean hasEnergy() {
        return currentEnergy > 0;
    }
}
