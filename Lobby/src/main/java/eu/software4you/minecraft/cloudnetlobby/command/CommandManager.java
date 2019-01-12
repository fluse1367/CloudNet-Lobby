package eu.software4you.minecraft.cloudnetlobby.command;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandManager implements CommandExecutor {
    private final HashMap<BaseCommand, MethodContainer> cmds;
    private final CommandMap cmap;
    private final JavaPlugin plugin;

    public CommandManager(final JavaPlugin plugin) {
        this.cmds = new HashMap<BaseCommand, MethodContainer>();
        this.plugin = plugin;
        CommandMap map;
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            map = null;
            ex.printStackTrace();
        }
        this.cmap = map;
        Lobby.debug("Getting CommandMap was " + ((this.cmap != null) ? "successfull" : "unsuccessfull"));
    }

    private void registerCommand(final String name) {
        if (this.cmap.getCommand(name) != null) {
            return;
        }
        final BukkitCommand cmd = new BukkitCommand(name);
        this.cmap.register(this.plugin.getName().toLowerCase(), cmd);
        cmd.setExecutor(this);
        Lobby.debug("Set executor for " + name);
    }

    private BaseCommand getCommand(final Command c, final CommandArgs args, final BaseCommand.Sender sender) {
        BaseCommand ret = null;
        for (final BaseCommand bc : this.cmds.keySet()) {
            if (bc.sender() != sender) {
                continue;
            }
            Lobby.debug("bc.sender() equals sender Line 53");
            if (!bc.command().equalsIgnoreCase(c.getName())) {
                continue;
            }
            Lobby.debug("bc.command() equals c.getName()! Line 53");
            if (args.isEmpty() && bc.subCommand().trim().isEmpty()) {
                Lobby.debug("args and subcommand are empty! Line 55");
                ret = bc;
            } else {
                if (args.isEmpty() || !bc.subCommand().equalsIgnoreCase(args.getString(0))) {
                    continue;
                }
                Lobby.debug("args are not empty and matched bc.subCommand() Line 58");
                ret = bc;
            }
        }
        Lobby.debug("returning \n\n" + ((ret != null) ? ret.toString() : "null") + " \n\nline 63");
        return ret;
    }

    private Object getCommandObject(final Command c, final BaseCommand.Sender sender, final CommandArgs args) throws Exception {
        BaseCommand bcmd = this.getCommand(c, args, sender);
        if (bcmd == null) {
            for (final BaseCommand bc : this.cmds.keySet()) {
                if (bc.sender() != sender) {
                    continue;
                }
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        Lobby.debug("returning \n\n" + ((bcmd != null) ? bcmd.toString() : "null") + " \n\nline 77");
        final MethodContainer container = this.cmds.get(bcmd);
        final Method me = container.getMethod(sender);
        return me.getDeclaringClass().newInstance();
    }

    public void registerClass(final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CommandHandler.class)) {
            this.plugin.getLogger().severe("Class is no CommandHandler");
            return;
        }
        final HashMap<BaseCommand, HashMap<BaseCommand.Sender, Method>> list = new HashMap<BaseCommand, HashMap<BaseCommand.Sender, Method>>();
        for (final Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(BaseCommand.class)) {
                Lobby.debug("BaseCommand is present. Line 98");
                final BaseCommand bc = m.getAnnotation(BaseCommand.class);
                this.registerCommand(bc.command());
                if (!list.containsKey(bc)) {
                    Lobby.debug("Putting empty hashmap in list Line 104");
                    list.put(bc, new HashMap<BaseCommand.Sender, Method>());
                }
                final HashMap<BaseCommand.Sender, Method> map = list.get(bc);
                map.put(bc.sender(), m);
                list.remove(bc);
                list.put(bc, map);
                Lobby.debug("Putting bc, map in list Line 114");
            }
        }
        for (final BaseCommand command : list.keySet()) {
            Lobby.debug("!!Registering command " + command.command() + " subcmd " + command.subCommand() + "!!");
            final HashMap<BaseCommand.Sender, Method> map2 = list.get(command);
            if (this.cmds.containsKey(command)) {
                final MethodContainer container = this.cmds.get(command);
                for (final BaseCommand.Sender s : container.getMethodMap().keySet()) {
                    final Method i = container.getMethod(s);
                    map2.put(s, i);
                }
                this.cmds.remove(command);
            }
            this.cmds.put(command, new MethodContainer(map2));
        }
    }

    private Method getMethod(final Command c, final BaseCommand.Sender sender, final CommandArgs args) {
        BaseCommand bcmd = this.getCommand(c, args, sender);
        if (bcmd == null) {
            for (final BaseCommand bc : this.cmds.keySet()) {
                if (bc.sender() != sender) {
                    continue;
                }
                if (bc.command().equalsIgnoreCase(c.getName()) && bc.subCommand().trim().isEmpty()) {
                    bcmd = bc;
                    break;
                }
            }
        }
        Lobby.debug("returning \n\n" + ((bcmd != null) ? bcmd.toString() : "null") + " \n\nline 144");
        final MethodContainer container = this.cmds.get(bcmd);
        final Method m = container.getMethod(sender);
        try {
            return m;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean executeCommand(final Command c, final CommandSender s, final String[] args) {
        CommandArgs a = CommandArgs.getArgs(args, 0);
        BaseCommand.Sender sender;
        if (s instanceof Player) {
            sender = BaseCommand.Sender.PLAYER;
        } else {
            sender = BaseCommand.Sender.CONSOLE;
        }
        final Method m = this.getMethod(c, sender, a);
        if (m != null) {
            m.setAccessible(true);
            final BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isEmpty() && bc.subCommand().equalsIgnoreCase(a.getString(0))) {
                a = CommandArgs.getArgs(args, 1);
            }
            CommandResult cr;
            try {
                if (sender == BaseCommand.Sender.PLAYER) {
                    final Player p = (Player) s;
                    if (bc.permission() != null && !bc.permission().trim().isEmpty()) {
                        if (!p.hasPermission(bc.permission())) {
                            cr = CommandResult.NO_PERMISSION;
                        } else {
                            cr = (CommandResult) m.invoke(this.getCommandObject(c, sender, a), p, a);
                        }
                    } else {
                        cr = (CommandResult) m.invoke(this.getCommandObject(c, sender, a), p, a);
                    }
                } else {
                    cr = (CommandResult) m.invoke(this.getCommandObject(c, sender, a), s, a);
                }
            } catch (Exception e) {
                e.printStackTrace();
                cr = CommandResult.NONE;
            }
            if (cr != null && cr.getMessage() != null) {
                final String perm = (bc.permission() != null) ? bc.permission() : "";
                s.sendMessage(cr.getMessage().replace("%cmd%", bc.command()).replace("%perm%", perm));
            }
        } else {
            s.sendMessage("§4The command was not made for your sender type!");
        }
        return true;
    }

    public boolean onCommand(final CommandSender cs, final Command cmnd, final String string, final String[] strings) {
        return this.executeCommand(cmnd, cs, strings);
    }
}
