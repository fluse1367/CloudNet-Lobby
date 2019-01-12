package eu.software4you.minecraft.cloudnetlobby.command;

import eu.software4you.minecraft.cloudnetlobby.configuration.Layout;
import org.bukkit.ChatColor;

public enum CommandResult {
    NONE(null),
    NO_PERMISSION(Layout.errorPermission.get()),
    USAGEERROR(Layout.errorUsage.get("%cmd%")),
    ONLY_PLAYER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "This command is only for players!"),
    NOT_ONLINE(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "That player is not online."),
    NOT_A_NUMBER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "It has to be a number!"),
    UNKNOWNERROR(Layout.errorUnknown.get()),
    ;

    private final String msg;

    CommandResult(final String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}
