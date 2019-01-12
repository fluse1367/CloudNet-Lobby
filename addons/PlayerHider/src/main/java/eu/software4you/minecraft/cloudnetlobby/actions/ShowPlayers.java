package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.EntityUtils;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ShowPlayers extends Action implements Listener {

    public ShowPlayers() {
        super("ShowPlayers");
    }

    public static Showing get(Player p) {
        return has(p) ? Showing.valueOf(String.valueOf(p.getMetadata("playersShowing").get(0).value())) : Showing.All;
    }

    public static boolean has(Player p) {
        return p.hasMetadata("playersShowing");
    }

    public static void set(Player p, Showing showing) {
        EntityUtils.getInstance().setMetadata(p, "playersShowing", showing.name());
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) throws Exception {
        Showing showing = !arg.equals("") ? Showing.valueOf(arg) : (has(caller) ? get(caller) : Showing.All);
        switch (showing) {
            case None:
                Bukkit.getOnlinePlayers().forEach(caller::hidePlayer);
                if (!quit) caller.sendMessage(Layout.prefix.get() + "Showing §cno other players");
                break;
            case OnlyFriends:
                for (Player pp : Bukkit.getOnlinePlayers()) {
                    if (!(Placeholder.isRegistered("IsFriend") && Placeholder.replace(caller, "IsFriend", pp.getUniqueId().toString()).equals("true"))) {
                        caller.hidePlayer(pp);
                    } else {
                        caller.showPlayer(pp);
                    }
                }
                if (!quit) caller.sendMessage(Layout.prefix.get() + "Showing §donly friends");
                break;
            case All:
            default:
                showing = Showing.All;
                Bukkit.getOnlinePlayers().forEach(caller::showPlayer);
                if (!quit) caller.sendMessage(Layout.prefix.get() + "Showing §aall players");
                break;
        }
        set(caller, showing);
        return true;
    }

    @Override
    protected void load() {
        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @Override
    protected void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            for (Player pp : Bukkit.getOnlinePlayers()) {
                if (e.getPlayer() != pp) call(pp, "", true);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public enum Showing {
        All, OnlyFriends, None;

        public static String[] names() {
            String[] array = ArrayUtils.EMPTY_STRING_ARRAY;
            for (Showing value : values())
                ArrayUtils.add(array, value.name());
            return array;
        }

        @Override
        public String toString() {
            return name();
        }
    }

}
