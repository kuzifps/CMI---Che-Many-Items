package com.fxfps.cmi;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmiMod implements ModInitializer {
    public static final String MOD_ID = "cmi";
    public static final String MOD_NAME = "CheManyItems (CMI)";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] Inicializando CheManyItems 🇦🇷 - Too Many Items Argentina", MOD_NAME);
    }
}
