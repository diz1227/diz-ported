package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class TriggerBotModule extends Module {

    private final Random random = new Random();

    // Random CPS timing
    private long nextAttackTime = 0L;

    // Queued delayed attack system
    private LivingEntity queuedTarget = null;
    private int queuedAttackTicks = 0;
    private boolean swungAlready = false;

    public TriggerBotModule() {
        super("TriggerBot", "Attacks players and mobs when looking at them.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        // Handle queued delayed attack
        if (queuedTarget != null) {

            // Always send swing packet even if attack may not happen
            if (!swungAlready) {
                mc.player.swing(InteractionHand.MAIN_HAND);
                swungAlready = true;
            }

            queuedAttackTicks--;

            if (queuedAttackTicks <= 0) {

                // Simulate mouse input packet
                mc.options.keyAttack.setDown(true);

                // Send actual attack packet
                mc.gameMode.attack(mc.player, queuedTarget);

                // Release mouse input
                mc.options.keyAttack.setDown(false);

                nextAttackTime = System.currentTimeMillis() + randomDelay();

                queuedTarget = null;
                swungAlready = false;
            }

            return;
        }

        HitResult hit = mc.hitResult;

        if (hit == null || hit.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) hit;

        if (!(entityHit.getEntity() instanceof Player) &&
            !(entityHit.getEntity() instanceof Mob)) return;

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        long currentTime = System.currentTimeMillis();

        // Randomized CPS delay
        if (currentTime < nextAttackTime) return;

        // Randomize required distance slightly closer than normal reach
        double extraCloseness = 0.1 + (random.nextDouble() * 0.1);

        // Slightly reduced vanilla-ish reach
        double maxReach = 3.0 - extraCloseness;

        if (mc.player.distanceTo(target) > maxReach) return;

        // Slight cooldown randomization
        float requiredCooldown = 0.90f + random.nextFloat() * 0.10f;

        if (mc.player.getAttackStrengthScale(0f) >= requiredCooldown) {

            // Small intentional miss chance
            if (random.nextFloat() < 0.05f) {
                nextAttackTime = currentTime + randomDelay();
                return;
            }

            // Queue delayed attack for randomized future tick
            queuedTarget = target;

            // Random 1-3 tick delay before attack packet
            queuedAttackTicks = 1 + random.nextInt(3);

            swungAlready = false;
        }
    }

    private long randomDelay() {
        // 90ms - 199ms random delay
        return 90 + random.nextInt(110);
    }
}
