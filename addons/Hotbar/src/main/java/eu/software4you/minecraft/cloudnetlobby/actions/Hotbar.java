package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.McStringUtils;
import eu.software4you.minecraft.PlayerUtils;
import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.parsing.ItemVariabler;
import eu.software4you.minecraft.cloudnetlobby.parsing.Variabler;
import eu.software4you.utils.ArrayUtils;
import eu.software4you.utils.JSONConfigurationUtils;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Hotbar extends Action {
    private final HashMap<String, JSONConfiguration> registeredHotbars = new HashMap<>();
    private final HashMap<Player, Handler> handlers = new HashMap<>();

    public Hotbar() {
        super("Hotbar");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) throws Exception {
        if (arg.equals("~done") && handlers.containsKey(caller))
            handlers.get(caller).done();
        else if (arg.equals("~reload") && handlers.containsKey(caller)) {
            String id = handlers.get(caller).id;
            handlers.get(caller).done();
            call(caller, id, quit);
        } else
            new Handler(caller, arg).begin();
        return true;
    }

    @Override
    protected void load() {
        new File(getDataFolder(), "hotbars").mkdirs();
        try {
            for (File hotbar : new File(getDataFolder(), "hotbars").listFiles(f -> f.getName().endsWith(".json"))) {
                JSONConfiguration json = new FileBasedConfigurationBuilder<>(JSONConfiguration.class).configure(new Parameters().fileBased().setFile(hotbar)).getConfiguration();
                registerHotbar(hotbar.getName().substring(0, hotbar.getName().lastIndexOf(".")), json);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unload() {
        registeredHotbars.clear();
        handlers.forEach((c, h) -> {
            c.getInventory().clear();
            h.done(false);
        });
        handlers.clear();
    }

    private void validateRegistered(String id, String thr) {
        if (!isHotbar(id))
            throw new IllegalAccessError(thr);
    }

    private void validateNotRegistered(String id, String thr) {
        if (isHotbar(id))
            throw new IllegalAccessError(thr);
    }

    public boolean isHotbar(String id) {
        return registeredHotbars.containsKey(id);
    }

    private void registerHotbar(String id, JSONConfiguration json) {
        validateNotRegistered(id, "Tried to register an already registered menu ('" + id + "')");
        registeredHotbars.put(id, json);
    }

    private void unregisterHotbar(String id) {
        validateRegistered(id, "Tried to unregister a non-registered menu ('" + id + ')');
        registeredHotbars.remove(id);
    }

    private class Handler implements Listener {
        private final JSONConfiguration json;
        private final Player caller;
        private final String id;
        private final HashMap<Integer, List<String>> actionsLeftClick = new HashMap<>();
        private final HashMap<Integer, List<String>> actionsRightClick = new HashMap<>();
        private final HashMap<Integer, ItemStack> hotbarItems = new HashMap<>();
        private boolean done = false;

        private Handler(Player caller, String id) throws Exception {
            if (!isHotbar(id))
                throw new IllegalAccessError("Tried to load an non-registered hotbar ('" + id + "') for "
                        + caller.getName());
            if (handlers.containsKey(caller))
                throw new IllegalAccessError("Tried to overload hotbar ('" + handlers.get(caller).id
                        + "') with menu ('" + id + "') for " + caller.getName());
            this.json = registeredHotbars.get(id);
            this.caller = caller;
            this.id = id;
            Variabler v = new Variabler(json, caller, "");
            handlers.put(caller, this);
        }

        private void begin() throws Exception {
            Bukkit.getPluginManager().registerEvents(this, lobby);
            parse();
            give();
        }

        private void done() {
            done(true);
        }

        private void done(boolean b) {
            if (done)
                return;
            done = true;
            HandlerList.unregisterAll(this);
            actionsLeftClick.clear();
            actionsRightClick.clear();
            hotbarItems.clear();
            if (b)
                handlers.remove(caller, this);
        }

        private void parse() throws Exception {
            hotbarItems.clear();
            actionsLeftClick.clear();
            actionsRightClick.clear();

            String title = McStringUtils.colorText(json.getString("title"));

            ItemVariabler iv = new ItemVariabler(json, "content", caller) {
                @Override
                public void item(int slot, ItemStack stack) {
                    hotbarItems.put(slot, stack);
                }

                @Override
                public void actionLeftClick(int slot, List<String> actions) {
                    actionsLeftClick.put(slot, actions);
                }

                @Override
                public void actionRightClick(int slot, List<String> actions) {
                    actionsRightClick.put(slot, actions);
                }
            };

            for (String key : JSONConfigurationUtils.getKeys(json, "content", false)) {
                iv.iteration(key.substring(key.lastIndexOf(".") + 1), json.getInt(key + ".slot"));
            }
        }

        private void give() {
            PlayerInventory inv = caller.getInventory();
            int slot = 0;
            for (ItemStack stack : inv.getContents()) {
                if (stack != null && !stack.getType().equals(Material.AIR) && !hotbarItems.containsKey(slot))
                    inv.removeItem(stack);
                slot++;
            }
            this.hotbarItems.forEach((i, s) -> {
                if (inv.getItem(i) == null || !inv.getItem(i).equals(s))
                    inv.setItem(i, s);
            });
        }

        @EventHandler
        public void onItemUse(PlayerInteractEvent e) {
            try {
                Player caller = e.getPlayer();
                if (
                        caller == this.caller // get sure the player is using this hotbar
                                && ArrayUtils.arrayContains(
                                new Object[]{org.bukkit.event.block.Action.LEFT_CLICK_AIR,
                                        org.bukkit.event.block.Action.LEFT_CLICK_BLOCK,
                                        org.bukkit.event.block.Action.RIGHT_CLICK_AIR,
                                        org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK},
                                e.getAction()
                        ) // get sure the player makes a left or a right click
                                && hotbarItems.containsKey(PlayerUtils.getCurrentHotbarSlot(caller)) // get sure the player holds an registered item
                ) {
                    int slot = PlayerUtils.getCurrentHotbarSlot(caller);
                    List<String> actions = null;
                    switch (e.getAction()) {
                        case LEFT_CLICK_AIR:
                        case LEFT_CLICK_BLOCK:
                            actions = actionsLeftClick.get(slot);
                            break;
                        case RIGHT_CLICK_AIR:
                        case RIGHT_CLICK_BLOCK:
                            actions = actionsRightClick.get(slot);
                            break;
                    }
                    if (actions != null)
                        Lobby.callActions(caller, actions);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            if (e.getPlayer() == caller)
                done();
        }
    }
}
