package eu.software4you.minecraft.cloudnetlobby.actions;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import org.bukkit.entity.Player;

public class Connect extends Action {
    public Connect() throws Exception {
        super("Connect");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) {
        if (!quit) {
            String group = arg.substring(0, arg.lastIndexOf("-"));
            ServerGroupMode mode = CloudAPI.getInstance().getServerGroup(group).getGroupMode();
            if (mode.equals(ServerGroupMode.LOBBY) || mode.equals(ServerGroupMode.STATIC_LOBBY))
                Layout.connectingLobby.send(caller, arg);
            else
                Layout.connectingServer.send(caller, arg);
        }
        PlayerExecutorBridge.INSTANCE.sendPlayer(CloudAPI.getInstance().getOnlinePlayer(caller.getUniqueId()), arg);
        return true;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
