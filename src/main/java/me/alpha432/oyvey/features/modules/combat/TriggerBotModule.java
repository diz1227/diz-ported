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

    // CPS timing
    private long nextAttackTime = 0L;

    // Delayed attack system
    private LivingEntity queuedTarget = null;
    private int queuedAttackTicks = 0;

    // Swing timing
    private int swingDelayTicks = 0;
    private boolean shouldSwing = false;

    // Reacquisition delay
    private int reacquireTicks = 0;
    private boolean hadTargetLastTick = false;

    // Micro hesitation tracking
    private int trackingTicks = 0;

    public TriggerBotModule() {
        super("TriggerBot", "Attacks players and mobs when looking at them.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        // Handle delayed swing timing
        if (shouldSwing) {
            swingDelayTicks--;

            if (swingDelayTicks <= 0) {
                mc.player.swing(InteractionHand.MAIN_HAND);

                // Rare double swing
                if (random.nextFloat() < 0.03f) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                shouldSwing = false;
            }
        }

        // Handle queued delayed attack
        if (queuedTarget != null) {

            queuedAttackTicks--;

            if (queuedAttackTicks <= 0) {

                // Simulated mouse input
                mc.options.keyAttack.setDown(true);

                // Actual attack packet
                mc.gameMode.attack(mc.player, queuedTarget);

                // Release mouse button
                mc.options.keyAttack.setDown(false);

                // Rare post-attack swing
                if (random.nextFloat() < 0.06f) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                nextAttackTime = System.currentTimeMillis() + randomDelay();

                queuedTarget = null;
            }

            return;
        }

        HitResult hit = mc.hitResult;

        if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
            hadTargetLastTick = false;
            trackingTicks = 0;
            return;
        }

        EntityHitResult entityHit = (EntityHitResult) hit;

        if (!(entityHit.getEntity() instanceof Player) &&
            !(entityHit.getEntity() instanceof Mob)) {

            hadTargetLastTick = false;
            trackingTicks = 0;
            return;
        }

        LivingEntity target = (LivingEntity) entityHit.getEntity();

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        // Reacquisition delay
        if (!hadTargetLastTick) {
            reacquireTicks = 1 + random.nextInt(2);
            hadTargetLastTick = true;
            trackingTicks = 0;
        }

        if (reacquireTicks > 0) {
            reacquireTicks--;
            return;
        }

        long currentTime = System.currentTimeMillis();

        // CPS delay
        if (currentTime < nextAttackTime) return;

        // Reduced randomized reach
        double extraCloseness = 0.1 + (random.nextDouble() * 0.1);
        double maxReach = 3.0 - extraCloseness;

        double distance = mc.player.distanceTo(target);

        if (distance > maxReach) {
            trackingTicks = 0;
            return;
        }

        // Require target tracking before attack
        trackingTicks++;

        int requiredTrackingTicks = 1 + random.nextInt(3);

        // Rarely require longer tracking
        if (random.nextFloat() < 0.10f) {
            requiredTrackingTicks = 4 + random.nextInt(3);
        }

        if (trackingTicks < requiredTrackingTicks) {

            // Occasional tracking swing without attack
            if (random.nextFloat() < 0.04f) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            return;
        }

        // Dynamic miss chance
        float missChance = 0.03f;

        // Higher edge-range miss chance
        if (distance > (maxReach - 0.15)) {
            missChance = 0.12f;
        }

        // Movement-based inconsistency
        if (!mc.player.onGround()) {
            missChance += 0.05f;
        }

        // Sprint turning hesitation
        if (mc.player.isSprinting()) {
            missChance += 0.02f;
        }

        // Occasional hesitation
        if (random.nextFloat() < 0.05f) return;

        // Randomized cooldown threshold
        float requiredCooldown = 0.90f + random.nextFloat() * 0.10f;

        // Rarely attack earlier
        if (random.nextFloat() < 0.10f) {
            requiredCooldown = 0.82f + random.nextFloat() * 0.06f;
        }

        if (mc.player.getAttackStrengthScale(0f) >= requiredCooldown) {

            // Dynamic miss chance
            if (random.nextFloat() < missChance) {
                nextAttackTime = currentTime + randomDelay();
                trackingTicks = 0;
                return;
            }

            queuedTarget = target;

            // Mostly 1-3 ticks, sometimes 4-6
            if (random.nextFloat() < 0.15f) {
                queuedAttackTicks = 4 + random.nextInt(3);
            } else {
                queuedAttackTicks = 1 + random.nextInt(3);
            }

            // Variable swing timing
            float swingRoll = random.nextFloat();

            if (swingRoll < 0.65f) {

                // Swing immediately
                mc.player.swing(InteractionHand.MAIN_HAND);

            } else if (swingRoll < 0.88f) {

                // Swing 1 tick later
                shouldSwing = true;
                swingDelayTicks = 1;

            } else if (swingRoll < 0.96f) {

                // Swing 2 ticks later
                shouldSwing = true;
                swingDelayTicks = 2;

            } else {

                // No swing before attack
                shouldSwing = false;
            }

            trackingTicks = 0;
        }
    }

    private long randomDelay() {

        // Burst clicking
        if (random.nextFloat() < 0.30f) {
            return 90 + random.nextInt(25);
        }

        // Normal pauses
        return 120 + random.nextInt(90);
    }
}
