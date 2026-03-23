package com.cipherman.quickorganizer;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickOrganizerMod implements ModInitializer {
    public static final String MOD_ID = "quickorganizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("QuickOrganizer initialized!");
    }
}
