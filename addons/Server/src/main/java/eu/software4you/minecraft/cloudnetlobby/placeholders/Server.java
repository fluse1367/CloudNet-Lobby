package eu.software4you.minecraft.cloudnetlobby.placeholders;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import org.bukkit.entity.Player;

public class Server extends Placeholder {
    public Server() {
        super("Server");
    }

    @Override
    protected String replace(Player caller, String arg) {
        CloudAPI ca = CloudAPI.getInstance();
        String cmd = arg;
        String server = ca.getServerId();

        if (arg.contains(":")) {
            String[] args = arg.split(":");
            cmd = args[0];
            server = arg.substring(cmd.length() + 1);
        }
        ServerInfo si = ca.getServerInfo(server);
        switch (cmd) {
            case "IsOnline":
                return si != null ? String.valueOf(si.isOnline()) : "false";
            case "MOTD":
                return si != null ? si.getMotd() : "null";
            case "Players":
                return si != null ? String.valueOf(si.getOnlineCount()) : "-1";
            case "MaxPlayers":
                return si != null ? String.valueOf(si.getMaxPlayers()) : "-1";
            case "Name":
                return server;
        }
        return id + ":" + arg;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
