package eu.software4you.minecraft.cloudnetlobby;

import eu.software4you.configuration.file.YamlConfiguration;
import eu.software4you.minecraft.McStringUtils;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.addons.Addon;
import eu.software4you.minecraft.cloudnetlobby.command.CommandManager;
import eu.software4you.minecraft.cloudnetlobby.configuration.Config;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import eu.software4you.minecraft.cloudnetlobby.configuration.Waypoints;
import eu.software4you.minecraft.cloudnetlobby.listeners.ChatListener;
import eu.software4you.minecraft.cloudnetlobby.listeners.PlayerListener;
import eu.software4you.minecraft.cloudnetlobby.listeners.SpawnListener;
import eu.software4you.minecraft.cloudnetlobby.listeners.WorldListener;
import eu.software4you.minecraft.cloudnetlobby.parsing.ReplaceEngine;
import eu.software4you.reflection.UniClass;
import eu.software4you.utils.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Lobby extends JavaPlugin {

    static Lobby instance;
    private static boolean debug = false;
    private CommandManager commandManager;
    private Logger logger;

    public static void debug(String s) {
        if (debug)
            instance.logger.info("[DEBUG] " + s);
    }

    public static void callActions(Player caller, List<String> actions) throws Exception {
        for (String action : actions) {
            callAction(caller, action);
        }
    }

    public static void callAction(Player caller, String line) throws Exception {
        Map.Entry<String, String> e = parseAddonCall(line);
        Action.call(caller, e.getKey(), e.getValue());
    }

    public static Map.Entry<String, String> parseAddonCall(String line) {
        String id = line.substring(0, line.contains(":") ? line.indexOf(":") : line.length());
        String arg = line.substring(id.length() + (line.contains(":") ? 1 : 0));
        return new AbstractMap.SimpleEntry<>(id, arg);
    }

    public static String replace(Player caller, String source) {
        return ReplaceEngine.fullReplace(caller, source);
    }

    public static String replaceC(Player caller, String source) {
        return McStringUtils.colorText(replace(caller, source));
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        debug = new File(getDataFolder(), "debug").exists();
        if (init())
            getLogger().info("Enabled!");
        else
            Bukkit.getPluginManager().disablePlugin(this);
    }

    private boolean init() {
        try {
            getLogger().info("Initiating ...");

            saveResources();

            Config.init(this);
            Layout.init(this);
            Waypoints.init(this);

            loadAddons();

            loadListeners();

            commandManager = new CommandManager(this);
            commandManager.registerClass(LobbyCommandHandler.class);

            getLogger().info("Initiated!");

            Bukkit.getOnlinePlayers().forEach(pp -> Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(pp, null)));
        } catch (Exception e) {
            Bukkit.broadcastMessage("§b§lCloudNet§6§lLobby §8»§c Error occurred while initiating. See console for details.");
            getLogger().warning("Error occurred while initiating:");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void saveResources() {
        extract("", "config.yml");
        extract("", "layout.yml");
        extract("", "waypoints.json");


        new File(getDataFolder(), "addons").mkdir();
    }

    private void extract(String dir, String file) {
        extract(dir, file, false);
    }

    private void extract(String dir, String file, boolean repalce) {
        FileUtils.saveResource(getClass(), "res/" + dir + (!dir.equals("") ? "/" : "") + file, new File(getDataFolder(), dir).getPath(), repalce, false);
    }

    private void loadListeners() {
        loadWorldlisteners();
        loadSpawnListeners();
        loadPlayerListeners();
        loadChatListeners();
    }

    private void loadWorldlisteners() {
        if (!Config.worldDaytimeChange.bool())
            new WorldListener.DaytimeChange().register();
        if (!Config.worldBlockChange.bool())
            new WorldListener.BlockChange().register(this);
        if (!Config.worldWeatherChange.bool())
            new WorldListener.WeatherChange().register(this);
    }

    private void loadSpawnListeners() {
        if (!Config.spawnAnimals.bool())
            new SpawnListener.Animals().register(this);
        if (!Config.spawnMonsters.bool())
            new SpawnListener.Monsters().register(this);
        if (!Config.spawnNpcs.bool())
            new SpawnListener.Npcs().register(this);
        if (!Config.spawnOthers.bool())
            new SpawnListener.Others().register(this);
    }

    private void loadPlayerListeners() {
        if (Config.playerBlockInventory.bool())
            new PlayerListener.InventoryBlock().register(this);
        if (!Config.playerDamageTake.bool())
            new PlayerListener.DamageTake().register(this);
        if (Config.playerDamageRefill.bool())
            new PlayerListener.DamageRefill().register(this);
        if (!Config.playerHungerTake.bool())
            new PlayerListener.HungerTake().register(this);
        if (Config.playerHungerRefill.bool())
            new PlayerListener.HungerRefill().register(this);
        if (!Config.playerPvp.bool())
            new PlayerListener.Pvp().register(this);
        if (!Config.playerPveMake.bool())
            new PlayerListener.PveMake().register(this);
        if (!Config.playerPveTake.bool())
            new PlayerListener.PveTake().register(this);

        new PlayerListener.JoinActions().register(this);
    }

    private void loadChatListeners() {
        if (Config.chatDisableJoinMessage.bool())
            new ChatListener.DisableJoinMessage().register(this);
        if (Config.chatDisableQuitMessage.bool())
            new ChatListener.DisableQuitMessage().register(this);
    }

    private void loadAddons() throws Exception {

        Field field = Addon.class.getDeclaredField("lobby");
        field.setAccessible(true);
        field.set(null, this);

        List<Addon> loadedAddons = new ArrayList<>();

        for (File file : new File(getDataFolder(), "addons").listFiles(f -> f.getName().toLowerCase().endsWith(".jar"))) {
            try {
                logger.info("Loading addon " + file.getName());
                Addon addon = (Addon) loadAddon(file);
                loadedAddons.add(addon);
                logger.info("Addon " + addon.getId() + " ('" + file.getName() + "') loaded!");
            } catch (Exception e) {
                getLogger().warning("Error while loading addon " + file.getName() + ":\n" + ExceptionUtils.getFullStackTrace(e));
            }
        }

        Method m = Addon.class.getDeclaredMethod("register");
        m.setAccessible(true);

        for (Addon addon : loadedAddons)
            try {
                logger.info("Registering addon " + addon.getId());
                m.invoke(addon);
                logger.info("Addon " + addon.getId() + " registered!");
            } catch (Exception e) {
                getLogger().warning("Error occurred while registering addon " + addon.getId() + ":\n" + ExceptionUtils.getFullStackTrace(e.getCause()));
            }
    }

    private Object loadAddon(File jarFile) throws Exception {
        ZipFile zip = new ZipFile(jarFile);

        ZipEntry entry = zip.getEntry("addon.yml");
        if (entry == null)
            throw new Exception("Addon file must have an addon description file ('addon.yml')");
        InputStream in = zip.getInputStream(entry);
        YamlConfiguration c = new YamlConfiguration();
        c.load(new InputStreamReader(in));
        in.close();
        String main = c.getString("main");
        if (main == null)
            throw new Exception("Addon main path cannot be null");


        ClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, getClass().getClassLoader());

        Class clazz = null;
        try {
            clazz = Class.forName(main, true, cl);
        } catch (ClassNotFoundException e) {
            throw new Exception("Cannot find main class " + main);
        }
        Constructor constructor;
        try {
            constructor = clazz.getDeclaredConstructor(Lobby.class);
        } catch (NoSuchMethodException e) {
            throw new Exception("Constructor of " + main + " must have only eu.software4you.cloudnetlobby.Lobby as parameter");
        }
        constructor.setAccessible(true);

        Object instance = constructor.newInstance(this);
        if (!(instance instanceof Addon))
            throw new Exception("Main class must be an instance of eu.software4you.cloudnetlobby.addons.Addon");
        return instance;
    }

    @Override
    public void onDisable() {
        try {
            disable();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        getLogger().info("Disabled!");
    }

    private void disable() throws NoSuchMethodException {
        boolean b = false;

        LinkedHashMap<String, Addon> registeredAddons = ((LinkedHashMap<String, Addon>) new UniClass(Addon.class).getField("registeredAddons").get(null));

        Method m = Addon.class.getDeclaredMethod("unregister");
        m.setAccessible(true);

        List<Addon> registeredAddonsList = new ArrayList<>();
        registeredAddonsList.addAll(registeredAddons.values());

        for (Addon addon : registeredAddonsList)
            try {
                logger.info("Unregistering addon " + addon.getId());
                m.invoke(addon);
                logger.info("Addon " + addon.getId() + " unregistered!");
            } catch (Exception e) {
                getLogger().warning("Error occurred while unregistering addon " + addon.getId() + ":\n" + ExceptionUtils.getFullStackTrace(e.getCause()));
                b = true;
            }

        registeredAddons.clear();

        if (b)
            Bukkit.broadcastMessage("§b§lCloudNet§6§lLobby §8»§c Error occurred while disabling. See console for details.");
    }
}
