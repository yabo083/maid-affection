package com.github.touhoumaidaffection;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TouhouMaidAffection.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> KISS =
            SOUNDS.register("kiss", () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TouhouMaidAffection.MOD_ID, "touhou_maid_affection.kiss")
            ));
}
