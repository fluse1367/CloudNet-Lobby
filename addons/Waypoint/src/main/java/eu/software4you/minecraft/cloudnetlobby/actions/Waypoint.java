package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import eu.software4you.minecraft.cloudnetlobby.configuration.Waypoints;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Waypoint extends Action {

    public Waypoint() throws Exception {
        super("Waypoint");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) {
        Location wp = Waypoints.getWaypoint(arg);
        if (wp == null) {
            Layout.waypointNotExist.send(caller);
            return false;
        }
        caller.teleport(wp);
        if (quit) return true;
        if (arg.equals("spawn"))
            Layout.teleportSpawn.send(caller, arg);
        else
            Layout.teleportOther.send(caller, arg);
        return true;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
