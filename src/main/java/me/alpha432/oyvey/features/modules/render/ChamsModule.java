package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.world.entity.player.Player;

public class ChamsModule extends Module {

    public static ChamsModule INSTANCE;

    private final Color visibleColor =
            new Color(0, 255, 255, 120);

    private final Color hiddenColor =
            new Color(255, 0, 0, 120);

    public ChamsModule() {
        super(
                "Chams",
                "Renders player chams.",
                Category.RENDER
        );

        INSTANCE = this;
    }

    public boolean shouldRender(Player player) {
        return isEnabled() && player != null;
    }

    public Color getVisibleColor() {
        return visibleColor;
    }

    public Color getHiddenColor() {
        return hiddenColor;
    }
}
