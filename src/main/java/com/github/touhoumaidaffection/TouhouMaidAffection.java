package com.github.touhoumaidaffection;

import com.github.touhoumaidaffection.network.KissMaidPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TouhouMaidAffection.MOD_ID)
public class TouhouMaidAffection {
    public static final String MOD_ID = "touhou_maid_affection";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            s -> PROTOCOL_VERSION.equals(s) || NetworkRegistry.ABSENT.equals(s) || NetworkRegistry.ACCEPTVANILLA.equals(s),
            s -> PROTOCOL_VERSION.equals(s) || NetworkRegistry.ABSENT.equals(s) || NetworkRegistry.ACCEPTVANILLA.equals(s)
    );

    public TouhouMaidAffection() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.github.touhoumaidaffection.ModConfig.SPEC);

        ModSounds.SOUNDS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);

        int id = 0;
        CHANNEL.registerMessage(id++, KissMaidPayload.class, KissMaidPayload::encode, KissMaidPayload::decode, KissMaidPayload::handle);

        MinecraftForge.EVENT_BUS.register(com.github.touhoumaidaffection.handler.KissMaidHandler.class);

        LOGGER.info("Touhou Maid: Affection loaded! Now you can kiss your maid~");
    }
}
