package com.github.touhoumaidaffection.handler;

import com.github.touhoumaidaffection.ModConfig;
import com.github.touhoumaidaffection.ModDataComponents;
import com.github.touhoumaidaffection.ModEffects;
import com.github.touhoumaidaffection.data.LifeLinkData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.UUID;

public class LifeLinkHandler {

    /**
     * Shift+right-click maid with chain to bind/unbind.
     */
    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (player.level().isClientSide) return;
        if (!(target instanceof EntityMaid maid)) return;
        if (!player.isShiftKeyDown()) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(Items.CHAIN)) return;

        // Prevent default maid interaction
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);

        LifeLinkData data = mainHand.getOrDefault(ModDataComponents.LIFE_LINK.get(), LifeLinkData.EMPTY);
        UUID maidUuid = maid.getUUID();

        if (data.isBound(maidUuid)) {
            // Unbind
            mainHand.set(ModDataComponents.LIFE_LINK.get(), data.withoutMaid(maidUuid));
            player.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                    SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            // Check max bindings
            int maxBinds = ModConfig.LIFE_LINK_MAX_BINDS.get();
            if (data.boundMaids().size() >= maxBinds) {
                return;
            }
            // Bind
            mainHand.set(ModDataComponents.LIFE_LINK.get(), data.withBoundMaid(maidUuid));
            player.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                    SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.0F, 1.2F);
            // Binding particles
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        maid.getX(), maid.getEyeY(), maid.getZ(),
                        20, 0.3, 0.5, 0.3, 0.1);
            }
        }
    }

    /**
     * Intercept incoming damage to player and absorb with chain energy.
     */
    @SubscribeEvent
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        float damage = event.getAmount();
        if (damage <= 0) return;

        // Search inventory for active life-link chains
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(Items.CHAIN)) continue;

            LifeLinkData data = stack.get(ModDataComponents.LIFE_LINK.get());
            if (data == null || !data.hasBoundMaids() || !data.hasEnergy()) continue;

            // Calculate share ratio based on average bound maid favorability
            float shareRatio = getShareRatio(player, data);
            float absorbed = Math.min(damage * shareRatio, data.currentEnergy());

            if (absorbed > 0) {
                stack.set(ModDataComponents.LIFE_LINK.get(), data.withDamage(absorbed));
                damage -= absorbed;
                if (damage <= 0) {
                    event.setAmount(0);
                    return;
                }
            }
        }
        event.setAmount(damage);
    }

    /**
     * Per-tick: recalculate max energy, regenerate energy, apply/remove indicator effect.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        // Only every 20 ticks (1 second)
        if (player.tickCount % 20 != 0) return;

        boolean hasActiveLink = false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(Items.CHAIN)) continue;

            LifeLinkData data = stack.get(ModDataComponents.LIFE_LINK.get());
            if (data == null || !data.hasBoundMaids()) continue;

            // Recalculate max energy from nearby alive bound maids
            float maxEnergy = calculateMaxEnergy(player, data);
            float currentEnergy = Math.min(data.currentEnergy(), maxEnergy);

            // Regenerate energy
            float regenRate = ModConfig.LIFE_LINK_REGEN_PER_SEC.get().floatValue();
            currentEnergy = Math.min(maxEnergy, currentEnergy + regenRate);

            stack.set(ModDataComponents.LIFE_LINK.get(), data.withEnergy(currentEnergy, maxEnergy));

            if (maxEnergy > 0 && currentEnergy > 0) {
                hasActiveLink = true;
            }
        }

        // Apply or remove indicator effect
        if (hasActiveLink) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.LIFE_LINK.getDelegate(), 40, 0, false, true, true));
        }
    }

    private static float calculateMaxEnergy(Player player, LifeLinkData data) {
        float coefficient = ModConfig.LIFE_LINK_ENERGY_COEFFICIENT.get().floatValue();
        float maxRange = 64.0f; // Only count nearby maids
        float total = 0;

        for (UUID maidUuid : data.boundMaids()) {
            Entity entity = ((ServerLevel) player.level()).getEntity(maidUuid);
            if (entity instanceof EntityMaid maid && maid.isAlive()
                    && maid.distanceTo(player) < maxRange) {
                int favLevel = maid.getFavorabilityManager().getLevel();
                total += (favLevel + 1) * coefficient;
            }
        }
        return total;
    }

    private static float getShareRatio(Player player, LifeLinkData data) {
        // Average favorability level of all bound, alive, nearby maids
        float totalLevel = 0;
        int count = 0;
        for (UUID maidUuid : data.boundMaids()) {
            Entity entity = ((ServerLevel) player.level()).getEntity(maidUuid);
            if (entity instanceof EntityMaid maid && maid.isAlive()) {
                totalLevel += maid.getFavorabilityManager().getLevel();
                count++;
            }
        }
        if (count == 0) return 0;

        float avgLevel = totalLevel / count;
        // Share ratio: level 0 = 15%, level 1 = 25%, level 2 = 35%, level 3 = 50%
        return switch ((int) avgLevel) {
            case 1 -> ModConfig.LIFE_LINK_SHARE_LEVEL_1.get().floatValue();
            case 2 -> ModConfig.LIFE_LINK_SHARE_LEVEL_2.get().floatValue();
            case 3 -> ModConfig.LIFE_LINK_SHARE_LEVEL_3.get().floatValue();
            default -> ModConfig.LIFE_LINK_SHARE_LEVEL_0.get().floatValue();
        };
    }
}
