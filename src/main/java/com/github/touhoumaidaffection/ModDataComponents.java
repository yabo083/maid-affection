package com.github.touhoumaidaffection;

import com.github.touhoumaidaffection.data.LifeLinkData;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(TouhouMaidAffection.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LifeLinkData>> LIFE_LINK =
            DATA_COMPONENTS.registerComponentType("life_link",
                    builder -> builder
                            .persistent(LifeLinkData.CODEC)
                            .networkSynchronized(LifeLinkData.STREAM_CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
