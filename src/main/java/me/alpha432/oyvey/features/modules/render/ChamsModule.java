package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import com.mojang.blaze3d.vertex.Tesselator;
import me.alpha432.oyvey.event.impl.render.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import org.lwjgl.opengl.GL11;

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

    @Override
    public void onRender3D(Render3DEvent event) {

        if (nullCheck()) return;

        for (Player player : mc.level.players()) {

            if (player == mc.player)
                continue;

            renderPlayerChams(player, mc.getFrameTime());
        }
    }

    private void renderPlayerChams(Player player, float partialTicks) {

        double x =
                player.xOld + (player.getX() - player.xOld) * partialTicks
                        - mc.getEntityRenderDispatcher().camera.getPosition().x();

        double y =
                player.yOld + (player.getY() - player.yOld) * partialTicks
                        - mc.getEntityRenderDispatcher().camera.getPosition().y();

        double z =
                player.zOld + (player.getZ() - player.zOld) * partialTicks
                        - mc.getEntityRenderDispatcher().camera.getPosition().z();

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        Color color = hiddenColor;

        GL11.glColor4f(
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                color.getAlpha() / 255f
        );

        GL11.glTranslated(x, y, z);

        AABB bb =
                player.getBoundingBox()
                        .move(
                                -player.getX(),
                                -player.getY(),
                                -player.getZ()
                        );

        LevelRenderer.renderLineBox(
                new com.mojang.blaze3d.vertex.PoseStack(),
                Tesselator.getInstance().getBuilder(),
                bb,
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                color.getAlpha() / 255f
        );

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
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
