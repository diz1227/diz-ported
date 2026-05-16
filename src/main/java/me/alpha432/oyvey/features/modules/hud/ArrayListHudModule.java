package me.alpha432.oyvey.features.modules.hud;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.render.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HudModule;

public class ArrayListHudModule extends HudModule {

    public ArrayListHudModule() {
        super("ArrayList", "Displays enabled modules", 5, 30);
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
                    -1
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
