package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.McStringUtils;
import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.parsing.ItemVariabler;
import eu.software4you.minecraft.cloudnetlobby.parsing.Variabler;
import eu.software4you.utils.JSONConfigurationUtils;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Menu extends Action {

    private final HashMap<String, JSONConfiguration> registeredMenus = new HashMap<>();
    private final HashMap<Player, Handler> handlers = new HashMap<>();

    public Menu() {
        super("Menu");
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
        new File(getDataFolder(), "menus").mkdirs();
        try {
            for (File menu : new File(getDataFolder(), "menus").listFiles(f -> f.getName().endsWith(".json"))) {
                JSONConfiguration json = new FileBasedConfigurationBuilder<>(JSONConfiguration.class).configure(new Parameters().fileBased().setFile(menu)).getConfiguration();
                registerMenu(menu.getName().substring(0, menu.getName().lastIndexOf(".")), json);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unload() {
        registeredMenus.clear();
        handlers.forEach((c, h) -> {
            c.closeInventory();
            h.done(false);
        });
        handlers.clear();
    }

    private void validateRegistered(String id, String thr) {
        if (!isMenu(id))
            throw new IllegalAccessError(thr);
    }

    private void validateNotRegistered(String id, String thr) {
        if (isMenu(id))
            throw new IllegalAccessError(thr);
    }

    public boolean isMenu(String id) {
        return registeredMenus.containsKey(id);
    }

    private void registerMenu(String id, JSONConfiguration json) {
        validateNotRegistered(id, "Tried to register an already registered menu ('" + id + "')");
        registeredMenus.put(id, json);
    }

    private void unregisterMenu(String id) {
        validateRegistered(id, "Tried to unregister a non-registered menu ('" + id + ')');
        registeredMenus.remove(id);
    }

    private class Handler implements Listener {
        private final JSONConfiguration json;
        private final Player caller;
        private final String id;
        private final HashMap<Integer, List<String>> actionsLeftClick = new HashMap<>();
        private final HashMap<Integer, List<String>> actionsRightClick = new HashMap<>();
        private final Inventory inventory;
        private boolean done = false;

        private Handler(Player caller, String id) throws Exception {
            if (!isMenu(id))
                throw new IllegalAccessError("Tried to load an non-registered menu ('" + id + "') for "
                        + caller.getName());
            if (handlers.containsKey(caller))
                throw new IllegalAccessError("Tried to overload menu ('" + handlers.get(caller).id
                        + "') with menu ('" + id + "') for " + caller.getName());
            this.json = registeredMenus.get(id);
            this.caller = caller;
            this.id = id;
            Variabler v = new Variabler(json, caller, "");
            this.inventory = Bukkit.createInventory(null,
                    v.jsonInt("rows") * 9, McStringUtils.colorText(v.jsonString("title")));
            handlers.put(caller, this);
        }

        private void begin() throws Exception {
            Bukkit.getPluginManager().registerEvents(this, lobby);
            parse();
            open();
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
            if (b)
                handlers.remove(caller, this);
        }

        private void parse() throws Exception {
            inventory.clear();
            actionsLeftClick.clear();
            actionsRightClick.clear();
            ItemVariabler iv = new ItemVariabler(json, "content", caller) {
                @Override
                public void item(int slot, ItemStack stack) {
                    inventory.setItem(slot, stack);
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

            for (final String key : JSONConfigurationUtils.getKeys(json, "content", false)) {
                final String id = key.substring(key.lastIndexOf(".") + 1);

                int row = Integer.valueOf(Lobby.replace(caller, String.valueOf(json.getProperty(key + ".location.row"))));
                int column = Integer.valueOf(Lobby.replace(caller, String.valueOf(json.getProperty(key + ".location.column"))));
                int slot = (row - 1) * 9 + column - 1;

                iv.iteration(id, slot);
            }
        }

        private void open() {
            caller.openInventory(inventory);
        }

        private boolean inventoryCheck(Inventory inventory) {
            return inventory != null && inventory.equals(this.inventory);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            try {
                Inventory inv = e.getClickedInventory();
                if (e.getWhoClicked() instanceof Player) { // get sure that a player clicked the inventory
                    Player caller = (Player) e.getWhoClicked();
                    if (caller == this.caller // get sure the player is our watching player
                            && inventoryCheck(inv) // get sure that player has click on this inventory
                            && e.getCurrentItem() != null // get sure that player clicked on an item
                    ) {
                        int slot = e.getSlot();
                        List<String> actions = null;
                        switch (e.getClick()) {
                            case LEFT:
                                actions = actionsLeftClick.get(slot);
                                break;
                            case RIGHT:
                                actions = actionsRightClick.get(slot);
                                break;
                        }
                        if (actions != null)
                            Lobby.callActions(caller, actions);
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            Player caller = (Player) e.getPlayer();
            if (caller == this.caller && inventoryCheck(e.getInventory())) {
                done();
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            if (e.getPlayer() == caller)
                done();
        }
    }
}
