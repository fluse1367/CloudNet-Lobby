package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import org.bukkit.entity.Player;

public class PlayersShowing extends Placeholder {

    public PlayersShowing() {
        super("PlayersShowing");
    }

    @Override
    protected String replace(Player caller, String arg) {
        if (caller == null)
            return "";
        ShowPlayers.Showing s = ShowPlayers.get(caller);
        if (s != null) return s.name();
        return ShowPlayers.Showing.All.name();
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
