package eu.software4you.minecraft.cloudnetlobby.addons;

import eu.software4you.configuration.InvalidConfigurationException;
import eu.software4you.configuration.file.YamlConfiguration;
import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public abstract class Addon {
    protected static final Lobby lobby = null;
    static final LinkedHashMap<String, Addon> registeredAddons = new LinkedHashMap<>();
    protected final String id;
    private final File dataFolder;
    private final File configFile;
    private final YamlConfiguration config;

    public Addon(String id) {
        this.id = id;
        this.dataFolder = new File(lobby.getDataFolder(), "addons/" + this.id);
        this.configFile = new File(this.dataFolder, "config.yml");
        if (configFile.exists())
            this.config = YamlConfiguration.loadConfiguration(this.configFile);
        else
            this.config = new YamlConfiguration();

    }

    protected static void registerAddon(Addon addon) throws Exception {
        addon.register();
    }

    private static void validateNotRegistered(String id, String thr) {
        if (isRegistered(id))
            throw new IllegalAccessError(thr);
    }

    static void validateRegistered(String id, String thr) {
        if (!isRegistered(id))
            throw new IllegalAccessError(thr);
    }

    public static boolean isRegistered(String id) {
        return registeredAddons.containsKey(id);
    }

    private void register() throws Exception {
        validateNotRegistered(id, "Tried to register an already registered addon ('" + id + "')");
        registeredAddons.put(id, this);
        load();
    }

    private void unregister() throws Exception {
        validateRegistered(id, "Tried to unregister a non-registered addon ('" + id + ')');
        registeredAddons.remove(id);
        unload();
    }

    public final String getId() {
        return this.id;
    }

    public final File getDataFolder() {
        this.dataFolder.mkdir();
        return this.dataFolder;
    }

    public final File getConfigFile() {
        FileUtils.createNewFile(this.configFile);
        return this.configFile;
    }

    public final YamlConfiguration getConfig() {
        FileUtils.createNewFile(this.configFile);
        return this.config;
    }

    public final void reloadConfig() {
        try {
            this.config.load(getConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public final void saveConfig() {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void saveResource(String file, boolean replace) {
        File f = new File(getDataFolder(), file);
        File p = new File(f.getParent());
        if (!p.exists())
            p.mkdirs();
        FileUtils.saveResource(getClass(), file, p.getPath(), replace, true);
    }

    protected abstract void load() throws Exception;

    protected abstract void unload() throws Exception;
}
