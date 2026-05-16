package me.alpha432.oyvey.mixin.render;

import me.alpha432.oyvey.features.modules.render.ChamsModule;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.entity.player.PlayerEntityRenderer;

import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerRenderer {

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRenderPre(CallbackInfo ci) {

        if (ChamsModule.INSTANCE == null
                || !ChamsModule.INSTANCE.isEnabled()) {
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glColor4f(
                1.0f,
                0.0f,
                0.0f,
                0.5f
        );
    }

    @Inject(
            method = "render",
            at = @At("RETURN")
    )
    private void onRenderPost(CallbackInfo ci) {

        if (ChamsModule.INSTANCE == null
                || !ChamsModule.INSTANCE.isEnabled()) {
            return;
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glColor4f(
                1.0f,
                1.0f,
                1.0f,
                1.0f
        );
    }
}
