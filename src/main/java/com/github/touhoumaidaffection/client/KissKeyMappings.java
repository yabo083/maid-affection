package com.github.touhoumaidaffection.client;

import com.github.touhoumaidaffection.TouhouMaidAffection;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KissKeyMappings {
    public static final String CATEGORY = "key.categories." + TouhouMaidAffection.MOD_ID;
    public static final KeyMapping KISS_CARRIED = new KeyMapping(
            "key." + TouhouMaidAffection.MOD_ID + ".kiss_carried",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    private KissKeyMappings() {
    }
}
