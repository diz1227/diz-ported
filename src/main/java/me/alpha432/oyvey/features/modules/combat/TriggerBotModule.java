package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class TriggerBotModule extends Module {

    private final Random random = new Random();

    // Timing
    private long nextAttackTime = 0L;

    // Queued attack system
    private LivingEntity queuedTarget = null;
    private int queuedAttackTicks = 0;

    // Swing timing
    private int swingDelayTicks = 0;
    private boolean shouldSwing = false;

    // Tracking
    private int trackingTicks = 0;
    private int reacquireTicks = 0;
    private boolean hadTargetLastTick = false;

    public TriggerBotModule() {
        super(
                "TriggerBot",
                "Automatically attacks entities under your crosshair.",
                Category.COMBAT,
        );
    }

    @Override
    public void onTick() {

        if (nullCheck()) return;

        // Tiny aim jitter / micro corrections
        if (queuedTarget != null && random.nextFloat() < 0.15f) {

            float yawJitter =
                    (random.nextFloat() - 0.5f) * 0.35f;

            float pitchJitter =
                    (random.nextFloat() - 0.5f) * 0.20f;

            mc.player.setYRot(mc.player.getYRot() + yawJitter);
            mc.player.setXRot(mc.player.getXRot() + pitchJitter);
        }

        // Delayed swing timing
        if (shouldSwing) {

            swingDelayTicks--;

            if (swingDelayTicks <= 0) {

                if (swing.getValue()) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                shouldSwing = false;
            }
        }

        // Handle queued delayed attack
        if (queuedTarget != null) {

            queuedAttackTicks--;

            if (queuedAttackTicks <= 0) {

                mc.gameMode.attack(mc.player, queuedTarget);

                // Rare double attack
                if (random.nextFloat() < 0.02f) {

                    mc.gameMode.attack(mc.player, queuedTarget);

                    if (swing.getValue()) {
                        mc.player.swing(InteractionHand.MAIN_HAND);
                    }
                }

                if (swing.getValue() && random.nextFloat() < 0.06f) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                nextAttackTime =
                        System.currentTimeMillis() + randomDelay();

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

        Entity entity = entityHit.getEntity();

        // Entity filtering
        if (entity instanceof Player && !players.getValue()) return;

        if (entity instanceof Mob && !mobs.getValue()) return;

        if (!(entity instanceof LivingEntity)) return;

        LivingEntity target = (LivingEntity) entity;

        if (target == mc.player) return;
        if (!target.isAlive()) return;

        // Reacquire delay
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
        double extraCloseness =
                0.1 + (random.nextDouble() * 0.1);

        double maxReach = 3.0 - extraCloseness;

        double distance = mc.player.distanceTo(target);

        if (distance > maxReach) {

            trackingTicks = 0;
            return;
        }

        // Tracking delay
        trackingTicks++;

        int requiredTrackingTicks =
                1 + random.nextInt(3);

        if (random.nextFloat() < 0.10f) {
            requiredTrackingTicks =
                    4 + random.nextInt(3);
        }

        if (trackingTicks < requiredTrackingTicks) {

            // Occasional tracking swing
            if (random.nextFloat() < 0.04f && swing.getValue()) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            return;
        }

        // Dynamic miss chance
        float missChance = 0.03f;

        // Edge range inconsistency
        if (distance > (maxReach - 0.15)) {
            missChance = 0.12f;
        }

        // Airborne inconsistency
        if (!mc.player.onGround()) {
            missChance += 0.05f;
        }

        // Sprint inconsistency
        if (mc.player.isSprinting()) {
            missChance += 0.02f;
        }

        // Occasional hesitation
        if (random.nextFloat() < 0.05f) return;

        // Randomized cooldown threshold
        float requiredCooldown =
                0.90f + random.nextFloat() * 0.10f;

        // Rare early attacks
        if (random.nextFloat() < 0.10f) {
            requiredCooldown =
                    0.82f + random.nextFloat() * 0.06f;
        }

        if (mc.player.getAttackStrengthScale(0f)
                >= requiredCooldown) {

            // Miss chance
            if (random.nextFloat() < missChance) {

                nextAttackTime =
                        currentTime + randomDelay();

                trackingTicks = 0;
                return;
            }

            queuedTarget = target;

            // Mostly 1-3 ticks, sometimes 4-6
            if (random.nextFloat() < 0.15f) {

                queuedAttackTicks =
                        4 + random.nextInt(3);

            } else {

                queuedAttackTicks =
                        1 + random.nextInt(3);
            }

            // Variable swing timing
            float swingRoll = random.nextFloat();

            if (swingRoll < 0.65f) {

                if (swing.getValue()) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

            } else if (swingRoll < 0.88f) {

                shouldSwing = true;
                swingDelayTicks = 1;

            } else if (swingRoll < 0.96f) {

                shouldSwing = true;
                swingDelayTicks = 2;

            } else {

                shouldSwing = false;
            }

            trackingTicks = 0;
        }
    }

    private long randomDelay() {

        // Burst clicking
        if (random.nextFloat() < 0.30f) {
            return minDelay.getValue()
                    + random.nextInt(25);
        }

        // Normal pauses
        return minDelay.getValue()
                + random.nextInt(
                Math.max(
                        1,
                        maxDelay.getValue()
                                - minDelay.getValue()
                )
        );
    }
}
