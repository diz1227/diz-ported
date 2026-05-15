package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module; 
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class AutoSprintMod implements ClientModInitializer {
 
    public static final String MOD_ID = "autosprint";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
 
    // Simple toggle — true means AutoSprint is active
    public static boolean enabled = true;
 
    @Override
    public void onInitializeClient() {
        LOGGER.info("[AutoSprint] Loaded! Sprint is {}.", enabled ? "ON" : "OFF");
    }
}
