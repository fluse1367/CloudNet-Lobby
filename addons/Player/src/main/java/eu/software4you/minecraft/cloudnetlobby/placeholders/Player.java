package eu.software4you.minecraft.cloudnetlobby.placeholders;

import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;

public class Player extends Placeholder {
    public Player() {
        super("Player");
    }

    @Override
    protected String replace(org.bukkit.entity.Player caller, String arg) {
        String cmd = arg.contains(":") ? arg.substring(0, arg.indexOf(":")) : arg;
        arg = arg.substring(cmd.length());

        switch (cmd) {
            case "Name":
                return caller.getName();
            case "DisplayName":
                return caller.getDisplayName();
            case "UUID":
                return caller.getUniqueId().toString();
            case "HasPerm":
                return String.valueOf(caller.hasPermission(arg));
        }
        return null;
    }

    @Override
    protected void load() {
    }

    @Override
    protected void unload() {
    }
}
