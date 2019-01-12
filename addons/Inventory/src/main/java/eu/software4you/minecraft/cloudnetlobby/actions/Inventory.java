package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import org.bukkit.entity.Player;

public class Inventory extends Action {

    public Inventory() throws Exception {
        super("Inventory");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) {
        switch (arg) {
            case "Close":
                caller.closeInventory();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
