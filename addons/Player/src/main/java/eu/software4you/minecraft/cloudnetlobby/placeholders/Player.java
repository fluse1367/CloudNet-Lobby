package eu.software4you.minecraft.cloudnetlobby.placeholders;

import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;

public class Player extends Placeholder {
    public Player() {
        super("Player");
    }

    @Override
    protected String replace(org.bukkit.entity.Player caller, String arg) {
        switch (arg) {
            case "Name":
                return caller.getName();
            case "DisplayName":
                return caller.getDisplayName();
            case "UUID":
                return caller.getUniqueId().toString();
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
