package eu.software4you.minecraft.cloudnetlobby.lists;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Count extends eu.software4you.minecraft.cloudnetlobby.addons.List {
    public Count() {
        super("Count");
    }

    @Override
    protected List<String> list(Player caller, String arg) {
        java.util.List<String> li = new ArrayList<>();
        if (arg.contains(",")) {
            int a = Integer.valueOf(arg.split(",")[0]);
            int b = Integer.valueOf(arg.split(",")[1]);
            for (int i = a; a <= b; a++)
                li.add(String.valueOf(i));
        }
        return li;
    }

    @Override
    protected void load() throws Exception {

    }

    @Override
    protected void unload() throws Exception {

    }
}
