package eu.software4you.minecraft.cloudnetlobby.addons;

import org.bukkit.entity.Player;

public abstract class List extends Addon {
    public List(String id) {
        super(id);
    }

    public static java.util.List<String> list(Player caller, String id, String arg) {
        validateRegistered(id, "Tried to call a non-registered list ('" + id + "')");
        Addon addon = registeredAddons.get(id);
        if (!(addon instanceof List))
            throw new UnsupportedOperationException("Tried to call addon ('" + id + "') as a list, but it isn't");
        return ((List) addon).list(caller, arg);
    }

    public static boolean isRegistered(String id) {
        return registeredAddons.containsKey(id) && registeredAddons.get(id) instanceof List;
    }

    abstract protected java.util.List<String> list(Player caller, String arg);
}
