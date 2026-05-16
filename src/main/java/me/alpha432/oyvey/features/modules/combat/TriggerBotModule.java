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
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;

        if (!(entityHit.getEntity() instanceof Player) &&
            !(entityHit.getEntity() instanceof Mob)) return;

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        // Randomize required distance slightly closer than normal reach
        double extraCloseness = 0.1 + (random.nextDouble() * 0.1);

        long currentTime = System.currentTimeMillis();

        // Wait until randomized attack timer passes
        if (currentTime < nextAttackTime) return;

        // Vanilla-ish reach check with slight reduction
        double maxReach = 3.0 - extraCloseness;

        if (mc.player.distanceTo(target) > maxReach) return;

        // Slight cooldown randomization
        float requiredCooldown = 0.90f + random.nextFloat() * 0.10f;

        if (mc.player.getAttackStrengthScale(0f) >= requiredCooldown) {

            // Small miss chance (optional)
            if (random.nextFloat() < 0.05f) {
                nextAttackTime = currentTime + randomDelay();
                return;
            }

            // Simulate normal left click attack
            mc.startAttack();

            // Set next randomized delay
            nextAttackTime = currentTime + randomDelay();
        }
    }

    private long randomDelay() {
        // 90ms - 199ms random delay
        return 90 + random.nextInt(110);
    }
}
