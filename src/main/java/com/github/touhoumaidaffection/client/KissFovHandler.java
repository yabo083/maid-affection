package com.github.touhoumaidaffection.client;

import com.github.touhoumaidaffection.ModConfig;
import com.github.touhoumaidaffection.TouhouMaidAffection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = TouhouMaidAffection.MOD_ID, value = Dist.CLIENT)
public class KissFovHandler {

    // Zoom state — all client-side, single-player safe
    private static long zoomStartTime = -1;
    private static int zoomInTicks;
    private static int holdTicks;
    private static int zoomOutTicks;
    private static float zoomStrength;

    /**
     * Trigger a smooth FOV zoom-in → hold → zoom-out sequence.
     * Called from KissClientHandler when the kiss packet arrives.
     */
    public static void trigger() {
        zoomStartTime = System.currentTimeMillis();
        zoomInTicks = ModConfig.FOV_ZOOM_IN_TICKS.get();
        holdTicks = ModConfig.FOV_HOLD_TICKS.get();
        zoomOutTicks = ModConfig.FOV_ZOOM_OUT_TICKS.get();
        zoomStrength = ModConfig.FOV_ZOOM_STRENGTH.get().floatValue();
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        if (zoomStartTime < 0) return;

        long elapsed = System.currentTimeMillis() - zoomStartTime;
        // Convert tick durations to millis (50ms per tick)
        float inMs = zoomInTicks * 50f;
        float holdMs = holdTicks * 50f;
        float outMs = zoomOutTicks * 50f;
        float totalMs = inMs + holdMs + outMs;

        if (elapsed > totalMs) {
            // Animation finished
            zoomStartTime = -1;
            return;
        }

        float factor;
        if (elapsed < inMs) {
            // Phase 1: zoom in — smoothstep ease-in
            float t = elapsed / inMs;
            factor = smoothstep(t);
        } else if (elapsed < inMs + holdMs) {
            // Phase 2: hold at max zoom
            factor = 1.0f;
        } else {
            // Phase 3: zoom out — smoothstep ease-out
            float t = (elapsed - inMs - holdMs) / outMs;
            factor = 1.0f - smoothstep(t);
        }

        // Apply: multiply the current FOV modifier by (1 - strength * factor)
        // strength=0.3 → at peak the FOV narrows to 70% of normal
        float modifier = 1.0f - zoomStrength * factor;
        event.setNewFovModifier(event.getNewFovModifier() * modifier);
    }

    /**
     * Smoothstep interpolation for natural-feeling zoom.
     */
    private static float smoothstep(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }
}
