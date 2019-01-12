package eu.software4you.minecraft.cloudnetlobby.placeholders;

import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import org.bukkit.entity.Player;

public class HeldSlot extends Placeholder {
    public HeldSlot() {
        super("HeldSlot");
    }

    @Override
    protected String replace(Player caller, String arg) {
        if (caller == null)
            return "";
        return String.valueOf(caller.getInventory().getHeldItemSlot());
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
