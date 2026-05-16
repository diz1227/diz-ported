package me.alpha432.oyvey.mixin.render;

import me.alpha432.oyvey.features.modules.render.ChamsModule;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(RenderPlayer.class)
public abstract class MixinPlayerRenderer {

    @Shadow
    @Final
    private ModelPlayer modelBipedMain;

    @Inject(
            method = "doRender",
            at = @At("HEAD")
    )
    private void renderChams(
            AbstractClientPlayer player,
            double x,
            double y,
            double z,
            float entityYaw,
            float partialTicks,
            CallbackInfo ci
    ) {

        if (ChamsModule.INSTANCE == null
                || !ChamsModule.INSTANCE.isEnabled()) {
            return;
        }

        Color hidden =
                ChamsModule.INSTANCE.getHiddenColor();

        Color visible =
                ChamsModule.INSTANCE.getVisibleColor();

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();

        GL11.glBlendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();

        // Hidden color
        GL11.glColor4f(
                hidden.getRed() / 255f,
                hidden.getGreen() / 255f,
                hidden.getBlue() / 255f,
                hidden.getAlpha() / 255f
        );

        modelBipedMain.render(
                player,
                0,
                0,
                0,
                player.rotationYaw,
                player.rotationPitch,
                0.0625f
        );

        // Visible color
        GlStateManager.enableDepth();

        GL11.glColor4f(
                visible.getRed() / 255f,
                visible.getGreen() / 255f,
                visible.getBlue() / 255f,
                visible.getAlpha() / 255f
        );

        modelBipedMain.render(
                player,
                0,
                0,
                0,
                player.rotationYaw,
                player.rotationPitch,
                0.0625f
        );

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }
}
