package eu.software4you.minecraft.cloudnetlobby.listeners;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.minecraft.cloudnetlobby.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerListener {
    public static class InventoryBlock implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryMove(InventoryMoveItemEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onItemDrop(PlayerDropItemEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onItemDrop(PlayerPickupItemEvent e) {
            e.setCancelled(true);
        }
    }

    public static class DamageTake implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onDamageTake(EntityDamageEvent e) {
            if (e.getEntity() instanceof Player)
                e.setCancelled(true);
        }
    }

    public static class DamageRefill implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        }
    }

    public static class HungerTake implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onHunger(FoodLevelChangeEvent e) {
            if (e.getEntity() instanceof Player)
                e.setCancelled(true);
        }
    }

    public static class HungerRefill implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onHunger(FoodLevelChangeEvent e) {
            if (e.getEntity() instanceof Player) {
                e.setCancelled(false);
                e.setFoodLevel(20);
            }
        }

        public void onJoin(PlayerJoinEvent e) {
            e.getPlayer().setFoodLevel(20);
        }
    }

    public static class Pvp implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onPvp(EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                e.setCancelled(true);
        }
    }

    public static class PveMake implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onPveMake(EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player && !(e.getEntity() instanceof Player))
                e.setCancelled(true);
        }
    }

    public static class PveTake implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onPveTake(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player) && e.getEntity() instanceof Player)
                e.setCancelled(true);
        }
    }

    public static class JoinActions implements Listener {
        public void register(Lobby lobby) {
            Bukkit.getPluginManager().registerEvents(this, lobby);
        }

        public void unregister() {
            HandlerList.unregisterAll(this);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent e) {
            try {
                Lobby.callActions(e.getPlayer(), Config.playerJoinActions.list());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
