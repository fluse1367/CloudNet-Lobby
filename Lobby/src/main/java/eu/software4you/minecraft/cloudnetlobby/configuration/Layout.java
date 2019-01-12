package eu.software4you.minecraft.cloudnetlobby.configuration;

import eu.software4you.configuration.file.YamlConfiguration;
import eu.software4you.minecraft.McStringUtils;
import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.bukkit.command.CommandSender;

import java.io.File;

public enum Layout {
    prefix("prefix"),
    teleportSpawn("teleport.spawn"),
    teleportOther("teleport.other"),
    connectingLobby("connecting.lobby"),
    connectingServer("connecting.server"),
    waypointNotExist("waypoint.not-exist"),
    join("join"),
    quit("quit"),
    helpReload("help.reload"),
    helpSetSpawn("help.setspawn"),
    helpSetWaypoint("help.setwaypoint"),
    helpRemWaypoint("help.remwaypoint"),
    reload("reload"),
    setSpawn("setspawn"),
    setWaypoint("setwaypoint"),
    remWaypoint("remwaypoint"),
    gamemodeUpdated("gamemode-updated"),
    errorUnknown("error.unknown"),
    errorUsage("error.usage"),
    errorPermission("error.permission"),
    ;

    private static Lobby lobby;
    private static File yamlFile;
    private static YamlConfiguration yaml;
    private final String path;

    Layout(String path) {
        this.path = path;
    }

    public static void init(Lobby lobby) {
        Layout.lobby = lobby;
        yamlFile = new File(lobby.getDataFolder(), "layout.yml");
        yaml = YamlConfiguration.loadConfiguration(yamlFile);
    }

    @Override
    public String toString() {
        return get();
    }

    public String get(String... replacements) {
        String s = yaml.getString(path);
        for (int i = 0; i < replacements.length; i++) {
            s = s.replace("%" + i, replacements[i]);
        }
        return McStringUtils.colorText(s);
    }

    public void send(CommandSender sender, String... replacements) {
        sender.sendMessage(get(replacements));
    }
}
