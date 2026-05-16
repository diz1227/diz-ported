package me.alpha432.oyvey.mixin.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import me.alpha432.oyvey.features.modules.render.ChamsModule;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

import org.lwjgl.opengl.GL11;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Shadow
    @Final
    private PlayerModel<AbstractClientPlayer> model;

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void renderChams(
            AbstractClientPlayer player,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
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

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        // Through walls
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        VertexConsumer hiddenBuffer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucent(
                                player.getSkinTextureLocation()
                        )
                );

        model.renderToBuffer(
                poseStack,
                hiddenBuffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                hidden.getRed() / 255f,
                hidden.getGreen() / 255f,
                hidden.getBlue() / 255f,
                hidden.getAlpha() / 255f
        );

        // Normal visible layer
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        VertexConsumer visibleBuffer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucent(
                                player.getSkinTextureLocation()
                        )
                );

        model.renderToBuffer(
                poseStack,
                visibleBuffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                visible.getRed() / 255f,
                visible.getGreen() / 255f,
                visible.getBlue() / 255f,
                visible.getAlpha() / 255f
        );

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
