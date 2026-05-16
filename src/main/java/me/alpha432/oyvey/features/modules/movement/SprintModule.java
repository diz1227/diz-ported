package me.alpha432.oyvey.features.modules.movement;
 
import me.alpha432.oyvey.features.modules.Module;
 
public class SprintModule extends Module {
 
    public SprintModule() {
        super("Sprint", "Auto sprints for you.", Category.MOVEMENT);
    }
 
    @Override
    public void onTick() {
        if (nullCheck()) return;
 
        mc.player.setSprinting(true);
    }
}
