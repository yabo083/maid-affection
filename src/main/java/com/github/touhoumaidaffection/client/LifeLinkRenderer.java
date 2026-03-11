package com.github.touhoumaidaffection.client;

import com.github.touhoumaidaffection.ModDataComponents;
import com.github.touhoumaidaffection.TouhouMaidAffection;
import com.github.touhoumaidaffection.data.LifeLinkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;

import java.util.UUID;

@EventBusSubscriber(modid = TouhouMaidAffection.MOD_ID, value = Dist.CLIENT)
public class LifeLinkRenderer {

    private static final DustParticleOptions RED_DUST =
            new DustParticleOptions(new Vector3f(0.9f, 0.1f, 0.1f), 0.6f);

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        if (player.level().isClientSide && player.tickCount % 2 != 0) return;

        // Only render when holding chain in either hand
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack activeChain = null;
        if (mainHand.is(Items.CHAIN)) activeChain = mainHand;
        else if (offHand.is(Items.CHAIN)) activeChain = offHand;

        if (activeChain == null) return;

        LifeLinkData data = activeChain.get(ModDataComponents.LIFE_LINK.get());
        if (data == null || !data.hasBoundMaids()) return;

        Vec3 playerPos = player.position().add(0, player.getEyeHeight() * 0.6, 0);

        for (UUID maidUuid : data.boundMaids()) {
            Entity entity = findEntityByUuid(maidUuid);
            if (entity == null || !entity.isAlive()) continue;

            Vec3 maidPos = entity.position().add(0, entity.getEyeHeight() * 0.6, 0);
            drawParticleLine(playerPos, maidPos, data.hasEnergy());
        }
    }

    private static void drawParticleLine(Vec3 from, Vec3 to, boolean hasEnergy) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        DustParticleOptions dust = hasEnergy ? RED_DUST :
                new DustParticleOptions(new Vector3f(0.4f, 0.4f, 0.4f), 0.4f);

        double dist = from.distanceTo(to);
        int particles = (int) (dist * 3);

        for (int i = 0; i <= particles; i++) {
            double t = (double) i / particles;
            double x = from.x + (to.x - from.x) * t;
            double y = from.y + (to.y - from.y) * t;
            double z = from.z + (to.z - from.z) * t;

            // Slight wave for visual interest
            double wave = Math.sin(t * Math.PI * 2 + mc.level.getGameTime() * 0.1) * 0.05;
            mc.level.addParticle(dust, x + wave, y + wave, z + wave, 0, 0, 0);
        }
    }

    private static Entity findEntityByUuid(UUID uuid) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity.getUUID().equals(uuid)) return entity;
        }
        return null;
    }
}
