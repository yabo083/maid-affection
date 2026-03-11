package com.github.touhoumaidaffection.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Visual indicator effect for active Life Link.
 * No gameplay logic — just shows the icon when the chain's virtual HP pool is active.
 */
public class LifeLinkEffect extends MobEffect {
    public LifeLinkEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xCC0000);
    }
}
