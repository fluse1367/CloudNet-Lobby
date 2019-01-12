package eu.software4you.minecraft.cloudnetlobby.listeners;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnListener {
    public static class Animals implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onsSpawn(EntitySpawnEvent e) {
            if (e.getEntity() instanceof org.bukkit.entity.Animals)
                e.setCancelled(true);
        }
    }

    public static class Monsters implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent e) {
            if (e.getEntity() instanceof org.bukkit.entity.Monster)
                e.setCancelled(true);
        }
    }

    public static class Npcs implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent e) {
            if (e.getEntity() instanceof org.bukkit.entity.NPC)
                e.setCancelled(true);
        }
    }

    public static class Others implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent e) {
            if (e.getEntity() instanceof org.bukkit.entity.LivingEntity
                    && !(e.getEntity() instanceof Player))
                e.setCancelled(true);
        }
    }
}
