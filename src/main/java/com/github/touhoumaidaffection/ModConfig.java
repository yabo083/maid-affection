package com.github.touhoumaidaffection;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec SPEC;

    // Kiss cooldown (tiered by favorability level)
    public static final ModConfigSpec.IntValue COOLDOWN_LEVEL_0;
    public static final ModConfigSpec.IntValue COOLDOWN_LEVEL_1;
    public static final ModConfigSpec.IntValue COOLDOWN_LEVEL_2;
    public static final ModConfigSpec.IntValue COOLDOWN_LEVEL_3;

    // Favorability
    public static final ModConfigSpec.IntValue FAVORABILITY_POINTS;
    public static final ModConfigSpec.IntValue FAVORABILITY_COOLDOWN;

    // Buff
    public static final ModConfigSpec.BooleanValue BUFF_ENABLED;
    public static final ModConfigSpec.IntValue BUFF_KISS_THRESHOLD;
    public static final ModConfigSpec.IntValue BUFF_KISS_WINDOW;
    public static final ModConfigSpec.IntValue BUFF_DURATION;
    public static final ModConfigSpec.IntValue BUFF_AMPLIFIER_LEVEL_0;
    public static final ModConfigSpec.IntValue BUFF_AMPLIFIER_LEVEL_1;
    public static final ModConfigSpec.IntValue BUFF_AMPLIFIER_LEVEL_2;
    public static final ModConfigSpec.IntValue BUFF_AMPLIFIER_LEVEL_3;

    // Particles
    public static final ModConfigSpec.IntValue PARTICLE_COUNT_MIN;
    public static final ModConfigSpec.IntValue PARTICLE_COUNT_EXTRA;

    // FOV zoom
    public static final ModConfigSpec.BooleanValue FOV_ZOOM_ENABLED;
    public static final ModConfigSpec.IntValue FOV_ZOOM_IN_TICKS;
    public static final ModConfigSpec.IntValue FOV_HOLD_TICKS;
    public static final ModConfigSpec.IntValue FOV_ZOOM_OUT_TICKS;
    public static final ModConfigSpec.DoubleValue FOV_ZOOM_STRENGTH;
    public static final ModConfigSpec.DoubleValue FOV_CARRIED_SIDE_OFFSET;
    public static final ModConfigSpec.DoubleValue FOV_CARRIED_FORWARD_OFFSET;
    public static final ModConfigSpec.DoubleValue FOV_CARRIED_VERTICAL_OFFSET;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Kiss cooldown settings (in ticks, 20 ticks = 1 second)",
                        "Cooldown decreases as the maid's favorability level increases")
               .push("cooldown");

        COOLDOWN_LEVEL_0 = builder
                .comment("Cooldown at favorability level 0 (default: 100 = 5 seconds)")
                .defineInRange("level0", 100, 0, 6000);

        COOLDOWN_LEVEL_1 = builder
                .comment("Cooldown at favorability level 1 (default: 60 = 3 seconds)")
                .defineInRange("level1", 60, 0, 6000);

        COOLDOWN_LEVEL_2 = builder
                .comment("Cooldown at favorability level 2 (default: 20 = 1 second)")
                .defineInRange("level2", 20, 0, 6000);

        COOLDOWN_LEVEL_3 = builder
                .comment("Cooldown at favorability level 3 / max (default: 0 = no cooldown)")
                .defineInRange("level3", 0, 0, 6000);

        builder.pop();

        builder.comment("Favorability gain settings")
               .push("favorability");

        FAVORABILITY_POINTS = builder
                .comment("Favorability points gained per kiss (default: 3)")
                .defineInRange("points", 3, 1, 100);

        FAVORABILITY_COOLDOWN = builder
                .comment("Favorability gain cooldown in ticks (default: 600 = 30 seconds)",
                         "This is separate from the interaction cooldown - prevents favorability farming")
                .defineInRange("cooldownTicks", 600, 0, 72000);

        builder.pop();

        builder.comment("Maid's Prayer buff settings",
                        "Triggered by kissing multiple times in a short window")
               .push("buff");

        BUFF_ENABLED = builder
                .comment("Enable the Maid's Prayer buff (default: true)")
                .define("enabled", true);

        BUFF_KISS_THRESHOLD = builder
                .comment("Number of kisses needed to trigger the buff (default: 3)")
                .defineInRange("kissThreshold", 3, 1, 20);

        BUFF_KISS_WINDOW = builder
                .comment("Time window in ticks to reach the kiss threshold (default: 200 = 10 seconds)")
                .defineInRange("kissWindowTicks", 200, 20, 6000);

        BUFF_DURATION = builder
                .comment("Buff duration in ticks (default: 600 = 30 seconds)")
                .defineInRange("durationTicks", 600, 20, 72000);

        builder.comment("Regeneration amplifier per favorability level (0 = I, 1 = II, etc.)",
                        "Higher levels can exceed vanilla limits — the power of love knows no bounds")
               .push("amplifier");

        BUFF_AMPLIFIER_LEVEL_0 = builder
                .comment("Amplifier at favorability level 0 (default: 0 = Regen I)")
                .defineInRange("level0", 0, 0, 255);

        BUFF_AMPLIFIER_LEVEL_1 = builder
                .comment("Amplifier at favorability level 1 (default: 1 = Regen II)")
                .defineInRange("level1", 1, 0, 255);

        BUFF_AMPLIFIER_LEVEL_2 = builder
                .comment("Amplifier at favorability level 2 (default: 2 = Regen III, beyond vanilla)")
                .defineInRange("level2", 2, 0, 255);

        BUFF_AMPLIFIER_LEVEL_3 = builder
                .comment("Amplifier at favorability level 3 (default: 4 = Regen V, far beyond vanilla)")
                .defineInRange("level3", 4, 0, 255);

        builder.pop();
        builder.pop();

        builder.comment("Particle settings")
               .push("particles");

        PARTICLE_COUNT_MIN = builder
                .comment("Minimum number of heart particles per kiss (default: 3)")
                .defineInRange("minCount", 3, 0, 50);

        PARTICLE_COUNT_EXTRA = builder
                .comment("Extra random particles (total = min + random(0..extra)) (default: 4)")
                .defineInRange("extraRandom", 4, 0, 50);

        builder.pop();

        builder.comment("FOV zoom effect on kiss",
                        "Creates a smooth 'lean-in' feeling by narrowing the FOV")
               .push("fov");

        FOV_ZOOM_ENABLED = builder
                .comment("Enable FOV zoom on kiss (default: true)")
                .define("enabled", true);

        FOV_ZOOM_IN_TICKS = builder
                .comment("Zoom-in duration in ticks (default: 4 = 0.2s)")
                .defineInRange("zoomInTicks", 4, 1, 40);

        FOV_HOLD_TICKS = builder
                .comment("Hold at max zoom duration in ticks (default: 3 = 0.15s)")
                .defineInRange("holdTicks", 3, 0, 40);

        FOV_ZOOM_OUT_TICKS = builder
                .comment("Zoom-out duration in ticks (default: 6 = 0.3s)")
                .defineInRange("zoomOutTicks", 6, 1, 60);

        FOV_ZOOM_STRENGTH = builder
                .comment("Zoom strength (0.0 = no zoom, 1.0 = full zoom to 0 FOV) (default: 0.85)")
                .defineInRange("strength", 0.85, 0.0, 0.95);

        FOV_CARRIED_SIDE_OFFSET = builder
                .comment("Princess-carry camera target side offset relative to player look direction",
                        "Negative = left, positive = right (default: 0.48)")
                .defineInRange("carriedSideOffset", 0.48, -1.5, 1.5);

        FOV_CARRIED_FORWARD_OFFSET = builder
                .comment("Princess-carry camera target forward offset (default: 0.16)")
                .defineInRange("carriedForwardOffset", 0.16, -1.0, 1.0);

        FOV_CARRIED_VERTICAL_OFFSET = builder
                .comment("Princess-carry camera target vertical offset from player eye (default: -0.10)")
                .defineInRange("carriedVerticalOffset", -0.10, -1.0, 1.0);

        builder.pop();

        SPEC = builder.build();
    }
}
