package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import org.bukkit.entity.Player;

public class PlayerCommand extends Action {
    public PlayerCommand() {
        super("PlayerCommand");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) throws Exception {
        caller.chat("/" + arg);
        return true;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
