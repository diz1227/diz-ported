package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class TriggerBotModule extends Module {

    private final Random random = new Random();

    // Random CPS timing
    private long nextAttackTime = 0L;

    public TriggerBotModule() {
        super("TriggerBot", "Attacks players and mobs when looking at them.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;;

        EntityHitResult entityHit = (EntityHitResult) hit;

        if (!(entityHit.getEntity() instanceof Player) &&
            !(entityHit.getEntity() instanceof Mob)) return;

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        long currentTime = System.currentTimeMillis();

        // Wait until randomized attack timer passes
        if (currentTime < nextAttackTime) return;

        // Slight cooldown randomization
        float requiredCooldown = 0.90f + random.nextFloat() * 0.10f;

        if (mc.player.getAttackStrengthScale(0f) >= requiredCooldown) {

            // Small miss chance (optional)
            if (random.nextFloat() < 0.05f) {
                nextAttackTime = currentTime + randomDelay();
                return;
            }

            mc.gameMode.attack(mc.player, target);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);

            // Set next randomized delay
            nextAttackTime = currentTime + randomDelay();
        }
    }

    private long randomDelay() {
        // 85ms - 140ms random delay
        return 85 + random.nextInt(55);
    }
}
