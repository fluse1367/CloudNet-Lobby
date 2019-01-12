package eu.software4you.minecraft.cloudnetlobby;

import eu.software4you.minecraft.cloudnetlobby.command.*;
import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import eu.software4you.minecraft.cloudnetlobby.configuration.Waypoints;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class LobbyCommandHandler {
    private final HelpPage helpPage;

    public LobbyCommandHandler() {
        helpPage = new HelpPage("  /clobby");
        helpPage.addPage("reload", Layout.helpReload.get());
        helpPage.addPage("setspawn", Layout.helpSetSpawn.get());
        helpPage.addPage("setwaypoint <name>", Layout.helpSetWaypoint.get());
        helpPage.addPage("remwaypoint <name>", Layout.helpRemWaypoint.get());
        helpPage.prepare();
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.CONSOLE, permission = "lobby.clobby")
    public CommandResult executeConsole(CommandSender sender, CommandArgs args) {
        if (args.getLength() == 0) {
            sender.sendMessage("§aCloudNet-Lobby v" + Lobby.instance.getDescription().getVersion() + " made by Software4You.eu (fluse1367, TheActualOne, BlockStudiosHD, DashBlizz, DashBlitz)");
        }
        helpPage.sendHelp(sender, args);
        return CommandResult.NONE;
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.PLAYER)
    public CommandResult executePlayer(Player sender, final CommandArgs args) {
        return this.executeConsole(sender, args);
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.CONSOLE, permission = "lobby.reload", subCommand = "reload")
    public CommandResult executeSubReloadConsole(CommandSender sender, final CommandArgs args) {
        Bukkit.getPluginManager().disablePlugin(Lobby.instance);
        Bukkit.getPluginManager().enablePlugin(Lobby.instance);
        Layout.reload.send(sender);
        return CommandResult.NONE;
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.PLAYER, permission = "lobby.reload", subCommand = "reload")
    public CommandResult executeSubReloadPlayer(Player sender, final CommandArgs args) {
        return this.executeSubReloadConsole(sender, args);
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.PLAYER, permission = "lobby.setspawn", subCommand = "setspawn")
    public CommandResult executeSubSetSpawnPlayer(Player sender, CommandArgs args) {
        Waypoints.setWaypoint(sender.getLocation(), "spawn");
        Layout.setSpawn.send(sender);
        return CommandResult.NONE;
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.PLAYER, permission = "lobby.setspawn", subCommand = "setwaypoint")
    public CommandResult executeSubSetWaypointPlayer(Player sender, CommandArgs args) {
        if (args.isEmpty())
            return CommandResult.USAGEERROR;
        String name;
        Waypoints.setWaypoint(sender.getLocation(), name = args.getString(0));
        Layout.setWaypoint.send(sender, name);
        return CommandResult.NONE;
    }

    @BaseCommand(command = "clobby", sender = BaseCommand.Sender.PLAYER, permission = "lobby.setspawn", subCommand = "remwaypoint")
    public CommandResult executeSubRemWaypointPlayer(Player sender, CommandArgs args) {
        if (args.isEmpty())
            return CommandResult.USAGEERROR;
        String name = args.getString(0);
        Location wp = Waypoints.getWaypoint(name);
        if (wp == null) {
            Layout.waypointNotExist.send(sender);
            return CommandResult.NONE;
        }
        if (!Waypoints.removeWaypoint(name)) {
            return CommandResult.UNKNOWNERROR;
        }
        Layout.remWaypoint.send(sender, name);
        return CommandResult.NONE;
    }
}
