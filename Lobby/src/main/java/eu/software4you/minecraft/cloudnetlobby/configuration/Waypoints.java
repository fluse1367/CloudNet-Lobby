package eu.software4you.minecraft.cloudnetlobby.configuration;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Waypoints {
    private static Lobby lobby;
    private static File jsonFile;
    private static JSONConfiguration json;

    public static void init(Lobby lobby) throws ConfigurationException {
        Waypoints.lobby = lobby;
        jsonFile = new File(lobby.getDataFolder(), "waypoints.json");
        json = new FileBasedConfigurationBuilder<>(JSONConfiguration.class).configure(new Parameters().fileBased().setFile(jsonFile)).getConfiguration();
    }

    public static Location getWaypoint(String name) {
        if (json.getProperty(name + ".world") == null)
            return null;
        World w = Bukkit.getWorld(json.getString(name + ".world"));
        if (w == null) return null;
        return new Location(w,
                json.getDouble(name + ".x"),
                json.getDouble(name + ".y"),
                json.getDouble(name + ".z"),
                json.getFloat(name + ".yaw"),
                json.getFloat(name + ".pitch")
        );
    }

    public static boolean setWaypoint(Location loc, String name) {
        json.setProperty(name + ".world", loc.getWorld().getName());
        json.setProperty(name + ".x", loc.getX());
        json.setProperty(name + ".y", loc.getY());
        json.setProperty(name + ".z", loc.getZ());
        json.setProperty(name + ".yaw", loc.getYaw());
        json.setProperty(name + ".pitch", loc.getPitch());
        return save();
    }

    public static boolean removeWaypoint(String name) {
        json.getKeys(name).forEachRemaining(s -> json.setProperty(s, null));
        return save();
    }

    private static boolean save() {
        try {
            json.write(new FileWriter(jsonFile));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
