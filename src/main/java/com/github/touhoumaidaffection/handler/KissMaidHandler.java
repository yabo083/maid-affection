package com.github.touhoumaidaffection.handler;

import com.github.touhoumaidaffection.ModConfig;
import com.github.touhoumaidaffection.ModEffects;
import com.github.touhoumaidaffection.ModSounds;
import com.github.touhoumaidaffection.network.KissMaidPayload;
import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class KissMaidHandler {

    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, List<Long>> KISS_TIMESTAMPS = new HashMap<>();

    private static Boolean carryOnLoaded = null;

    private static boolean isCarryOnLoaded() {
        if (carryOnLoaded == null) {
            carryOnLoaded = ModList.get().isLoaded("carryon");
        }
        return carryOnLoaded;
    }

    private static long getCooldownForLevel(int level) {
        return switch (level) {
            case 1 -> ModConfig.COOLDOWN_LEVEL_1.get();
            case 2 -> ModConfig.COOLDOWN_LEVEL_2.get();
            case 3 -> ModConfig.COOLDOWN_LEVEL_3.get();
            default -> ModConfig.COOLDOWN_LEVEL_0.get();
        };
    }

    @SubscribeEvent
    public static void onInteractMaid(InteractMaidEvent event) {
        Player player = event.getPlayer();
        EntityMaid maid = event.getMaid();

        // Only trigger when sneaking with empty main hand
        if (!player.isShiftKeyDown() || !event.getStack().isEmpty()) {
            return;
        }

        // CarryOn compatibility: when CarryOn is loaded, it uses sneak + both hands empty
        // to pick up entities. Only trigger kiss when offhand is NOT empty to avoid conflict.
        if (isCarryOnLoaded() && player.getOffhandItem().isEmpty()) {
            return;
        }

        // Only on server side
        if (player.level().isClientSide) {
            event.setCanceled(true);
            return;
        }

        // Tiered cooldown check based on maid's favorability level
        long currentTick = player.level().getGameTime();
        int favLevel = maid.getFavorabilityManager().getLevel();
        long cooldown = getCooldownForLevel(favLevel);

        Long lastKiss = COOLDOWNS.get(player.getUUID());
        if (cooldown > 0 && lastKiss != null && (currentTick - lastKiss) < cooldown) {
            event.setCanceled(true);
            return;
        }

        // Cancel to prevent opening the maid GUI
        event.setCanceled(true);

        // Record cooldown
        COOLDOWNS.put(player.getUUID(), currentTick);

        // Apply favorability (dynamic Type with configured values)
        int favPoints = ModConfig.FAVORABILITY_POINTS.get();
        int favCooldown = ModConfig.FAVORABILITY_COOLDOWN.get();
        Type kissType = new Type("Kiss", favPoints, favCooldown);
        maid.getFavorabilityManager().apply(kissType);

        // Make the maid look at the player
        maid.getLookControl().setLookAt(player, 30.0F, 30.0F);

        // Play kiss sound at the midpoint between player and maid
        double midX = (player.getX() + maid.getX()) / 2.0;
        double midY = (player.getEyeY() + maid.getEyeY()) / 2.0;
        double midZ = (player.getZ() + maid.getZ()) / 2.0;
        player.level().playSound(null, midX, midY, midZ,
                ModSounds.KISS.get(), SoundSource.PLAYERS,
                1.0F, 1.0F);

        // Broadcast particle packet to all tracking clients
        KissMaidPayload payload = new KissMaidPayload(maid.getId(), player.getId());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(maid, payload);

        // Buff system: track kiss timestamps and check threshold
        if (ModConfig.BUFF_ENABLED.get()) {
            handleBuffTrigger(player, maid, currentTick, favLevel);
        }
    }

    private static int getAmplifierForLevel(int level) {
        return switch (level) {
            case 1 -> ModConfig.BUFF_AMPLIFIER_LEVEL_1.get();
            case 2 -> ModConfig.BUFF_AMPLIFIER_LEVEL_2.get();
            case 3 -> ModConfig.BUFF_AMPLIFIER_LEVEL_3.get();
            default -> ModConfig.BUFF_AMPLIFIER_LEVEL_0.get();
        };
    }

    private static void handleBuffTrigger(Player player, EntityMaid maid, long currentTick, int favLevel) {
        UUID playerId = player.getUUID();
        int threshold = ModConfig.BUFF_KISS_THRESHOLD.get();
        long window = ModConfig.BUFF_KISS_WINDOW.get();

        List<Long> timestamps = KISS_TIMESTAMPS.computeIfAbsent(playerId, k -> new ArrayList<>());
        timestamps.add(currentTick);

        // Remove timestamps outside the window
        timestamps.removeIf(t -> (currentTick - t) > window);

        if (timestamps.size() >= threshold) {
            // Clear timestamps to reset counter
            timestamps.clear();

            int duration = ModConfig.BUFF_DURATION.get();
            int amplifier = getAmplifierForLevel(favLevel);

            // Apply Maid's Prayer (custom effect with built-in regeneration) to both
            player.addEffect(new MobEffectInstance(
                    ModEffects.MAIDS_PRAYER.getDelegate(), duration, amplifier, false, true, true));
            maid.addEffect(new MobEffectInstance(
                    ModEffects.MAIDS_PRAYER.getDelegate(), duration, amplifier, false, true, true));
        }
    }
}
