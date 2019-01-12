package eu.software4you.minecraft.cloudnetlobby.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {
    private CommandExecutor exe;

    protected BukkitCommand(final String name) {
        super(name);
        this.exe = null;
    }

    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (this.exe != null) {
            this.exe.onCommand(sender, this, commandLabel, args);
        }
        return false;
    }

    public void setExecutor(final CommandExecutor exe) {
        this.exe = exe;
    }
}
