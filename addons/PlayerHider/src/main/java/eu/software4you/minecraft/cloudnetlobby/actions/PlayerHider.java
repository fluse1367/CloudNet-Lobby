package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Addon;

public class PlayerHider extends Addon {

    public PlayerHider() {
        super("PlayerHider");
    }

    @Override
    protected void load() throws Exception {
        registerAddon(new PlayersShowing());
        registerAddon(new ShowPlayers());
    }

    @Override
    protected void unload() {
    }
}
