package eu.software4you.minecraft.cloudnetlobby.listeners;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener {
    public static class DaytimeChange {
        public void register() {
            Bukkit.getWorlds().forEach(w -> w.setGameRuleValue("doDaylightCycle", "false"));
        }

        public void unregister() {
            Bukkit.getWorlds().forEach(w -> w.setGameRuleValue("doDaylightCycle", "true"));
        }
    }

    public static class WeatherChange implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onWeatherChange(WeatherChangeEvent e) {
            e.setCancelled(true);
        }
    }

    public static class BlockChange implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onBlockChange(BlockFromToEvent e) {
            e.setCancelled(true);
        }
    }
}
