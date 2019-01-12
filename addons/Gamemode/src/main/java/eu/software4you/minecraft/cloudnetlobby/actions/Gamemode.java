package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import eu.software4you.utils.CheckUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Gamemode extends Action {
    public Gamemode() throws Exception {
        super("GameMode");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) {
        GameMode gm = null;
        if (CheckUtils.isInteger(arg)) {
            switch (Integer.parseInt(arg)) {
                case 0:
                    gm = GameMode.SURVIVAL;
                    break;
                case 1:
                    gm = GameMode.CREATIVE;
                    break;
                case 2:
                    gm = GameMode.ADVENTURE;
                    break;
                case 3:
                    gm = GameMode.SPECTATOR;
                    break;
            }
        } else {
            gm = GameMode.valueOf(arg);
        }
        if (gm == null)
            gm = GameMode.SURVIVAL;
        caller.setGameMode(gm);
        if (!quit)
            Layout.gamemodeUpdated.send(caller, WordUtils.capitalize(gm.name().toLowerCase()));
        return true;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
