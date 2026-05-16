package me.alpha432.oyvey.features.modules.hud;

import me.alpha432.oyvey.event.impl.render.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.HudModule;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.OyVey;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayListHudModule extends HudModule {

    private final Setting<Float> scale = flt("Scale", 1.0f, 0.5f, 3.0f);

    public ArrayListHudModule() {
        super("ArrayList", "Displays enabled modules", 5, 30);

        register(scale);
    }

    @Override
    protected void render(Render2DEvent e) {
        super.render(e);

        List<Module> enabledModules = OyVey.moduleManager.modules.stream()
                .filter(Module::isEnabled)
                .filter(module -> module != this)
                .sorted(Comparator.comparingInt(
                        module -> -mc.font.width(module.getName())
                ))
                .collect(Collectors.toList());

        int yOffset = 0;
        int maxWidth = 0;

        for (Module module : enabledModules) {
            String text = module.getName();

            int drawX = (int) getX();
            int drawY = (int) getY() + yOffset;

            e.getContext().pose().pushPose();
            e.getContext().pose().scale(scale.getValue(), scale.getValue(), 1.0f);

            mc.font.draw(
                    e.getContext().pose(),
                    text,
                    drawX / scale.getValue(),
                    drawY / scale.getValue(),
                    -1
            );

            e.getContext().pose().popPose();

            int width = (int) (mc.font.width(text) * scale.getValue());
            if (width > maxWidth) {
                maxWidth = width;
            }

            yOffset += (int) ((mc.font.lineHeight + 2) * scale.getValue());
        }

        setWidth(maxWidth);
        setHeight(yOffset);
    }
}
