package eu.software4you.minecraft.cloudnetlobby.lists;

import de.dytanic.cloudnet.api.CloudAPI;
import eu.software4you.minecraft.cloudnetlobby.addons.List;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerGroups extends List {
    public ServerGroups() {
        super("ServerGroups");
    }

    @Override
    protected java.util.List<String> list(Player caller, String arg) {
        java.util.List<String> li = new ArrayList<>();
        AtomicInteger online = new AtomicInteger(0);
        CloudAPI.getInstance().getServers(arg).forEach(si -> {
            if (si.isOnline())
                online.incrementAndGet();
        });
        for (int i = 0; i < online.get(); i++)
            li.add(arg + "-" + (i + 1));
        return li;
    }

    @Override
    protected void load() throws Exception {

    }

    @Override
    protected void unload() throws Exception {

    }
}
