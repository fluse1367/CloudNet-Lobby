package eu.software4you.minecraft.cloudnetlobby.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandArgs {
    private final String[] args;
    private final int length;

    private CommandArgs(final String[] args, final int length) {
        this.args = args;
        this.length = length;
    }

    public static CommandArgs getArgs(final String[] args, final int start) {
        String a = "";
        int length = 0;
        for (int i = start; i < args.length; ++i) {
            a = a + args[i] + ";";
            ++length;
        }
        return new CommandArgs(a.split(";"), length);
    }

    public String getString(final int number) {
        return this.args[number];
    }

    public boolean isInteger(final int number) {
        try {
            Integer.valueOf(this.args[number]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble(final int number) {
        try {
            Double.valueOf(this.args[number]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isEmpty() {
        return this.length < 1;
    }

    public int getLength() {
        return this.length;
    }

    public String[] getArgs() {
        return this.args;
    }

    public boolean isPlayer(final int num) {
        return Bukkit.getPlayer(this.args[num]) != null;
    }

    public int getInt(final int num) {
        return Integer.valueOf(this.args[num]);
    }

    public OfflinePlayer getOfflinePlayer(final int num) {
        return Bukkit.getOfflinePlayer(this.args[num]);
    }

    public Player getPlayer(final int i) {
        return Bukkit.getPlayer(this.args[i]);
    }
}
