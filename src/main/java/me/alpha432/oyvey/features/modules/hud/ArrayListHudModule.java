package me.alpha432.oyvey.features.modules.hud;

import java.awt.Color;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.render.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HudModule;

public class ArrayListHudModule extends HudModule {
    private final Setting<Color> color =
        new Setting<>("Color", new Color(255, 255, 255, 255));

    public ArrayListHudModule() {
        super("ArrayList", "Displays enabled modules", 5, 30);
        register(color);
    }

    @Override
    protected void render(Render2DEvent e) {
        super.render(e);

        int yOffset = 0;
        int maxWidth = 0;

        for (Module module : OyVey.moduleManager.getModules()) {

            if (!module.isEnabled()) continue;
            if (module == this) continue;

            String text = module.getName();

            e.getContext().drawString(
                    mc.font,
                    text,
                    (int) getX(),
                    (int) getY() + yOffset,
                    color.getValue().getRGB() | 0xFF000000
            );

            int width = mc.font.width(text);

            if (width > maxWidth) {
                maxWidth = width;
            }

            yOffset += mc.font.lineHeight + 2;
        }

        setWidth(maxWidth);
        setHeight(yOffset);
    }
}
