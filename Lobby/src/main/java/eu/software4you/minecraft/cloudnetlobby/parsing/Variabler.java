package eu.software4you.minecraft.cloudnetlobby.parsing;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import org.apache.commons.configuration2.JSONConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Variabler {
    private final JSONConfiguration json;
    private final Player caller;
    private String prefix;
    private String var;
    private String varRepl;

    public Variabler(JSONConfiguration json, Player caller, String var) {
        this(json, caller, "", var);
    }

    public Variabler(JSONConfiguration json, Player caller, String prefix, String var) {
        this(json, caller, prefix, var, "");
    }

    public Variabler(JSONConfiguration json, Player caller, String prefix, String var, String varRepl) {
        this.json = json;
        this.caller = caller;
        this.prefix = prefix;
        this.var = var;
        this.varRepl = varRepl;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setVarRepl(String varRepl) {
        this.varRepl = varRepl;
    }

    public int jsonInt(String key) {
        return Integer.valueOf(jsonString(key));
    }

    public String jsonString(String key) {
        return replString(json.getProperty(prefix + key));
    }

    public List<String> jsonStringList(String key) {
        List<String> li = new ArrayList<>();
        List<Object> l = json.getList(prefix + key);
        if (l != null) {
            for (Object o : l) {
                li.add(replString(o));
            }
        }
        return li;
    }

    private String replString(Object source) {
        return Lobby.replace(caller, String.valueOf(source).replace("$" + var, varRepl));
    }

}