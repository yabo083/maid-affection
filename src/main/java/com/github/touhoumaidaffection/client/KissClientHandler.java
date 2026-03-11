package com.github.touhoumaidaffection.client;

import com.github.touhoumaidaffection.ModConfig;
import com.github.touhoumaidaffection.network.KissMaidPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KissClientHandler {
    public static void handle(KissMaidPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity maid = level.getEntity(payload.maidEntityId());
            Entity player = level.getEntity(payload.playerEntityId());
            if (maid == null || player == null) return;

            // Trigger FOV zoom if this is the local player
            if (player == Minecraft.getInstance().player && ModConfig.FOV_ZOOM_ENABLED.get()) {
                KissFovHandler.trigger();
            }

            // Calculate midpoint between player mouth and maid face
            Vec3 playerEye = player.getEyePosition();
            Vec3 maidEye = maid.getEyePosition();
            Vec3 midpoint = playerEye.add(maidEye).scale(0.5).add(0, -0.3, 0);

            int count = ModConfig.PARTICLE_COUNT_MIN.get() + level.random.nextInt(ModConfig.PARTICLE_COUNT_EXTRA.get() + 1);
            for (int i = 0; i < count; i++) {
                double offsetX = (level.random.nextDouble() - 0.5) * 0.5;
                double offsetY = level.random.nextDouble() * 0.3;
                double offsetZ = (level.random.nextDouble() - 0.5) * 0.5;

                level.addParticle(
                        ParticleTypes.HEART,
                        midpoint.x() + offsetX,
                        midpoint.y() + offsetY,
                        midpoint.z() + offsetZ,
                        0.0D,
                        0.1D,
                        0.0D
                );
            }
        });
    }
}
