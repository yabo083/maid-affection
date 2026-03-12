package com.github.touhoumaidaffection;

import com.github.touhoumaidaffection.effect.MaidsPrayerEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, TouhouMaidAffection.MOD_ID);

    public static final RegistryObject<MobEffect> MAIDS_PRAYER =
            MOB_EFFECTS.register("maids_prayer", MaidsPrayerEffect::new);
}
