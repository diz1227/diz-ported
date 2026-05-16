package me.alpha432.oyvey.features.modules.render;

import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class FakePlayerModule extends Module {

    private RemotePlayer fakePlayer;

    public FakePlayerModule() {
        super("FakePlayer", "Spawns a fake practice player.", Category.RENDER);
    }

    @Override
    public void onEnable() {

        if (nullCheck()) {
            disable();
            return;
        }

        fakePlayer = new RemotePlayer(
                mc.level,
                new GameProfile(UUID.randomUUID(), "FakePlayer")
        );

        // Copy your current position/rotation
        fakePlayer.copyPosition(mc.player);

        fakePlayer.setYRot(mc.player.getYRot());
        fakePlayer.setXRot(mc.player.getXRot());

        // Copy health
        fakePlayer.setHealth(mc.player.getHealth());

        // Assign fake entity ID
        fakePlayer.setId(-1337);

        // Spawn fake player
        mc.level.putNonPlayerEntity(fakePlayer.getId(), fakePlayer);
    }

    @Override
    public void onDisable() {

        if (fakePlayer == null || mc.level == null) {
            return;
        }

        // Remove fake player
        mc.level.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);

        fakePlayer = null;
    }
}
