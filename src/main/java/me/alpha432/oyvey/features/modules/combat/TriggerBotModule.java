package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBotModule extends Module {

    public TriggerBotModule() {
        super("TriggerBot", "Attacks players and mobs when looking at them.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        // Check what the crosshair is currently targeting
        HitResult hit = mc.crosshairTarget;
        if (hit == null) return;
        if (hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;

        // Only target players and mobs
        if (!(entityHit.getEntity() instanceof PlayerEntity) &&
            !(entityHit.getEntity() instanceof MobEntity)) return;

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        // Don't attack ourselves or dead entities
        if (target == mc.player) return;
        if (!target.isAlive()) return;

        // Attack using the interact controller (respects cooldown)
        if (mc.player.getAttackCooldownProgress(0f) >= 1f) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        }
    }
}
