package eu.software4you.minecraft.cloudnetlobby.placeholders;

import eu.software4you.configuration.file.YamlConfiguration;
import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import eu.software4you.sql.SqlEngine;
import eu.software4you.utils.Utils;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IsFriend extends Placeholder {
    private final SqlEngine engine;

    public IsFriend() {
        super("IsFriend");

        YamlConfiguration c = getConfig();
        c.options().copyDefaults(true);
        c.addDefault("mysql.host", "HOST");
        c.addDefault("mysql.user", "USER");
        c.addDefault("mysql.password", "PASSWORD");
        c.addDefault("mysql.database", "DATABASE");
        c.addDefault("enabled", false);
        saveConfig();
        engine = Utils.newSQLEngine();
    }

    @Override
    protected String replace(Player caller, String arg) {
        if (!getConfig().getBoolean("enabled"))
            return "false";
        try {
            if (!engine.isConnected())
                return "false";
            String fr1 = "";
            ResultSet rs = engine.query("SELECT player_id FROM `fr_players` WHERE player_uuid = '" + caller.getUniqueId().toString() + "'");
            if (rs.next())
                fr1 = rs.getString(1);
            String fr2 = "";
            rs = engine.query("SELECT player_id FROM `fr_players` WHERE player_uuid = '" + arg + "'");
            if (rs.next())
                fr2 = rs.getString(1);
            return engine.query(
                    "select * from fr_friend_assignment WHERE (friend1_id = " + fr1 + " AND friend2_id = " + fr2 + ") OR (friend1_id = " + fr2 + " AND friend2_id = " + fr2 + ")"
            ).next() ? "true" : "false";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "IsFriend_ERR";
    }

    @Override
    protected void load() {
        if (!getConfig().getBoolean("enabled"))
            return;
        YamlConfiguration c = getConfig();
        String host, user, password, database;
        SqlEngine.ConnectionData cData = new SqlEngine.ConnectionData(host = c.getString("mysql.host"),
                user = c.getString("mysql.user"), password = c.getString("mysql.password"),
                database = c.getString("mysql.database"));
        engine.setConnectionData(cData);
        try {
            engine.connect();
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.err.println("Cloud not connect to " + user + "@" + host + " (Using Password: " + (password != null && !password.equals("") ? "YES" : "NO") + "): \n" + e.getMessage());
        }
    }

    @Override
    protected void unload() throws SQLException {
        if (!getConfig().getBoolean("enabled"))
            return;
        if (engine.isConnected())
            engine.disconnect();
    }
}
