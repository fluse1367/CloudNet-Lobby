package eu.software4you.minecraft.cloudnetlobby.addons;

import org.bukkit.entity.Player;

public abstract class Placeholder extends Addon {
    public Placeholder(String id) {
        super(id);
    }

    public static String replace(Player caller, String id, String arg) {
        validateRegistered(id, "Tried to call a non-registered placeholder ('" + id + "')");
        Addon addon = registeredAddons.get(id);
        if (!(addon instanceof Placeholder))
            throw new UnsupportedOperationException("Tried to call addon ('" + id + "') as a placeholder, but it isn't");
        String ret = ((Placeholder) addon).replace(caller, arg);
        if (ret == null)
            ret = "null";
        return ret;
    }

    public static boolean isRegistered(String id) {
        return registeredAddons.containsKey(id) && registeredAddons.get(id) instanceof Placeholder;
    }

    abstract protected String replace(Player caller, String arg);
}
