package com.github.maidaffection;

import com.github.maidaffection.client.KissClientHandler;
import com.github.maidaffection.handler.KissMaidHandler;
import com.github.maidaffection.network.KissMaidPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MaidAffection.MOD_ID)
public class MaidAffection {
    public static final String MOD_ID = "maid_affection";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public MaidAffection(IEventBus modEventBus) {
        // Register sound events
        ModSounds.SOUNDS.register(modEventBus);

        // Register network packets on mod bus
        modEventBus.addListener(this::registerPayloads);

        // Register game event handlers on NeoForge bus
        NeoForge.EVENT_BUS.register(KissMaidHandler.class);

        LOGGER.info("Touhou Maid: Affection loaded! Now you can kiss your maid~");
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0").optional();
        registrar.playToClient(
                KissMaidPayload.TYPE,
                KissMaidPayload.STREAM_CODEC,
                KissClientHandler::handle
        );
    }
}
