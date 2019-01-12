package eu.software4you.minecraft.cloudnetlobby.addons;

import org.bukkit.entity.Player;

public abstract class Action extends Addon {
    public Action(String id) {
        super(id);
    }

    public static void call(Player caller, String id, String arg) throws Exception {
        boolean quit = id.startsWith("!");
        if (quit) id = id.substring(1);
        validateRegistered(id, "Tried to call a non-registered action ('" + id + "')");
        Addon addon = registeredAddons.get(id);
        if (!(addon instanceof Action))
            throw new UnsupportedOperationException("Tried to call addon ('" + id + "') as an action, but it isn't");
        ((Action) addon).call(caller, arg, quit);
    }

    public static boolean isRegistered(String id) {
        return registeredAddons.containsKey(id) && registeredAddons.get(id) instanceof Action;
    }

    abstract protected boolean call(Player caller, String arg, boolean quit) throws Exception;
}
