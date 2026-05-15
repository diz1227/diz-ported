package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;

public class SprintModule extends Module {

    public SprintModule() {
        super("Sprint", "Auto sprints for you.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;

        boolean isSneaking    = mc.player.isSneaking();
        boolean isUsingItem   = mc.player.isUsingItem();
        boolean tooHungry     = mc.player.getHungerManager().getFoodLevel() <= 6;
        boolean movingForward = mc.player.input != null && mc.player.input.playerInput.forward();

        if (!isSneaking && !isUsingItem && !tooHungry && movingForward) {
            mc.player.setSprinting(true);
        }
    }
}
