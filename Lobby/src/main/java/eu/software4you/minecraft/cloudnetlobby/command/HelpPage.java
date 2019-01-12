package eu.software4you.minecraft.cloudnetlobby.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpPage {
    private final List<CommandHelp> helpPages;
    private final List<String> HELP_TEXT;
    private final String command;

    public HelpPage(final String command) {
        this.helpPages = new ArrayList<CommandHelp>();
        this.HELP_TEXT = new ArrayList<String>();
        this.command = command;
    }

    public void addPage(final String argument, final String description) {
        if (argument.isEmpty()) {
            this.helpPages.add(new CommandHelp(this.command, description));
        } else {
            this.helpPages.add(new CommandHelp(this.command + " " + argument, description));
        }
    }

    public void prepare() {
        if (this.helpPages == null || this.helpPages.isEmpty()) {
            return;
        }
        this.HELP_TEXT.add("§2§m----§2[ §9Help §2]§m----");
        for (final CommandHelp ch : this.helpPages) {
            this.HELP_TEXT.add(ch.getText());
        }
        this.HELP_TEXT.add("§2§m------");
    }

    public boolean sendHelp(final CommandSender s, final CommandArgs args) {
        if (args.getLength() == 1 && (args.getString(0).equalsIgnoreCase("?") || args.getString(0).equalsIgnoreCase("help")) && !this.HELP_TEXT.isEmpty()) {
            for (final String string : this.HELP_TEXT) {
                s.sendMessage(string);
            }
            return true;
        }
        return false;
    }

    private class CommandHelp {
        private final String FULL_TEXT;

        public CommandHelp(final String cmd, final String description) {
            this.FULL_TEXT = ChatColor.GOLD + cmd + ChatColor.GRAY + " - " + description;
        }

        public String getText() {
            return this.FULL_TEXT;
        }
    }
}
