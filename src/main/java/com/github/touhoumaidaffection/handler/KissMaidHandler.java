package com.github.touhoumaidaffection.handler;

import com.github.touhoumaidaffection.ModSounds;
import com.github.touhoumaidaffection.network.KissMaidPayload;
import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KissMaidHandler {
    /**
     * Favorability type for kissing: +3 points, 600 tick (30s) cooldown for favorability gain
     */
    public static final Type KISS_FAVORABILITY = new Type("Kiss", 3, 600);

    /**
     * Interaction cooldown to prevent particle spam: 40 ticks (2 seconds)
     */
    private static final long KISS_COOLDOWN_TICKS = 40L;

    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

    private static Boolean carryOnLoaded = null;

    private static boolean isCarryOnLoaded() {
        if (carryOnLoaded == null) {
            carryOnLoaded = ModList.get().isLoaded("carryon");
        }
        return carryOnLoaded;
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

        // Interaction cooldown check
        long currentTick = player.level().getGameTime();
        Long lastKiss = COOLDOWNS.get(player.getUUID());
        if (lastKiss != null && (currentTick - lastKiss) < KISS_COOLDOWN_TICKS) {
            event.setCanceled(true);
            return;
        }

        // Cancel to prevent opening the maid GUI
        event.setCanceled(true);

        // Record cooldown
        COOLDOWNS.put(player.getUUID(), currentTick);

        // Apply favorability (has its own internal cooldown of 600 ticks)
        maid.getFavorabilityManager().apply(KISS_FAVORABILITY);

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
    }
}
