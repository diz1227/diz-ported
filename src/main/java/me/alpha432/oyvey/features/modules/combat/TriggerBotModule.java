package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TriggerBotModule extends Module {

    public TriggerBotModule() {
        super("TriggerBot", "Attacks players and mobs when looking at them.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        // Use the built-in module keybind — must be held to attack
        if (!getBind().isEmpty() && !getBind().isDown()) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;

        if (!(entityHit.getEntity() instanceof Player) &&
            !(entityHit.getEntity() instanceof Mob)) return;

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        if (mc.player.getAttackStrengthScale(0f) >= 1f) {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }
}
