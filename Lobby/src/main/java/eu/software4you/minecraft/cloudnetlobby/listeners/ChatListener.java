package eu.software4you.minecraft.cloudnetlobby.listeners;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener {
    public static class DisableJoinMessage implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent e) {
            e.setJoinMessage(null);
        }
    }

    public static class DisableQuitMessage implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onQuit(PlayerQuitEvent e) {
            e.setQuitMessage(null);
        }
    }
}
