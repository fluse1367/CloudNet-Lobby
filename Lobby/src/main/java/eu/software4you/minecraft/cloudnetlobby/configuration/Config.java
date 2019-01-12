package eu.software4you.minecraft.cloudnetlobby.configuration;

import eu.software4you.configuration.file.YamlConfiguration;
import eu.software4you.minecraft.cloudnetlobby.Lobby;

import java.io.File;
import java.util.List;

public enum Config {
    worldDaytimeChange("world.daytimeChange"),
    worldBlockChange("world.blockChange"),
    worldWeatherChange("world.weatherChange"),
    spawnAnimals("spawn.animals"),
    spawnMonsters("spawn.monsters"),
    spawnNpcs("spawn.npcs"),
    spawnOthers("spawn.others"),
    playerBlockInventory("player.blockInventory"),
    playerDamageTake("player.damage.take"),
    playerDamageRefill("player.damage.refill"),
    playerHungerTake("player.hunger.take"),
    playerHungerRefill("player.hunger.refill"),
    playerPvp("player.pvp"),
    playerPveMake("player.pve.make"),
    playerPveTake("player.pve.take"),
    playerJoinActions("player.joinactions"),
    chatDisableJoinMessage("chat.disable.joinMessage"),
    chatDisableQuitMessage("chat.disable.quitMessage"),
    ;

    private static Lobby lobby;
    private static File yamlFile;
    private static YamlConfiguration yaml;
    private final String path;

    Config(String path) {
        this.path = path;
    }

    public static void init(Lobby lobby) {
        Config.lobby = lobby;
        yamlFile = new File(lobby.getDataFolder(), "config.yml");
        yaml = YamlConfiguration.loadConfiguration(yamlFile);
    }

    @Override
    public String toString() {
        return string();
    }

    public String string() {
        return yaml.getString(path);
    }

    public int inte() {
        return yaml.getInt(path);
    }

    public boolean bool() {
        return yaml.getBoolean(path);
    }

    public List<String> list() {
        return yaml.getStringList(path);
    }
}
