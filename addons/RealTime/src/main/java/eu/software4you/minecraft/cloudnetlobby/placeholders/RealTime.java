package eu.software4you.minecraft.cloudnetlobby.placeholders;

import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTime extends Placeholder {
    public RealTime() {
        super("RealTime");
    }

    @Override
    protected String replace(Player caller, String arg) {
        return new SimpleDateFormat(arg).format(new Date());
    }

    @Override
    protected void load() throws Exception {

    }

    @Override
    protected void unload() throws Exception {

    }
}
