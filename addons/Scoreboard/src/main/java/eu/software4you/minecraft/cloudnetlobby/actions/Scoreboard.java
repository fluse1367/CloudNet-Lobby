package eu.software4you.minecraft.cloudnetlobby.actions;

import eu.software4you.minecraft.EntityUtils;
import eu.software4you.minecraft.McStringUtils;
import eu.software4you.minecraft.ScoreboardManager;
import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.minecraft.cloudnetlobby.addons.Action;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scoreboard extends Action {

    private final HashMap<String, JSONConfiguration> registeredScoreboards = new HashMap<>();
    private final HashMap<Player, Handler> handlers = new HashMap<>();

    public Scoreboard() {
        super("Scoreboard");
    }

    @Override
    protected boolean call(Player caller, String arg, boolean quit) throws Exception {
        if (arg.equals("~done") && handlers.containsKey(caller))
            handlers.get(caller).done();
        else if (arg.equals("~reload") && handlers.containsKey(caller)) {
            String id = handlers.get(caller).id;
            handlers.get(caller).done();
            call(caller, id, quit);
        } else
            new Handler(caller, arg).begin();
        return true;
    }

    @Override
    protected void load() {
        new File(getDataFolder(), "scoreboards").mkdirs();
        try {
            for (File file : new File(getDataFolder(), "scoreboards").listFiles(f -> f.getName().endsWith(".json"))) {
                JSONConfiguration json = new FileBasedConfigurationBuilder<>(JSONConfiguration.class).configure(new Parameters().fileBased().setFile(file)).getConfiguration();
                registerScoreboard(file.getName().substring(0, file.getName().lastIndexOf(".")), json);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unload() {
        registeredScoreboards.clear();
        handlers.forEach((c, h) -> {
            c.closeInventory();
            h.done(false);
        });
        handlers.clear();
    }

    private void validateRegistered(String id, String thr) {
        if (!isScoreboard(id))
            throw new IllegalAccessError(thr);
    }

    private void validateNotRegistered(String id, String thr) {
        if (isScoreboard(id))
            throw new IllegalAccessError(thr);
    }

    public boolean isScoreboard(String id) {
        return registeredScoreboards.containsKey(id);
    }

    private void registerScoreboard(String id, JSONConfiguration json) {
        validateNotRegistered(id, "Tried to register an already registered scoreboard ('" + id + "')");
        registeredScoreboards.put(id, json);
    }

    private class Handler implements Listener {
        private final JSONConfiguration json;
        private final Player caller;
        private final String id;
        private final ScoreboardManager man;

        private String title = "";
        private double updateIntervall = -1;
        private HashMap<SidebarElement, Integer> scores = new HashMap<>();
        private HashMap<String, Integer> simpleScores = new HashMap<>();
        private Thread ticker = null;

        private boolean done = false;

        private Handler(Player caller, String id) throws Exception {
            if (!isScoreboard(id))
                throw new IllegalAccessError("Tried to load an non-registered scoreboard ('" + id + "') for "
                        + caller.getName());
            if (handlers.containsKey(caller))
                throw new IllegalAccessError("Tried to overload scoreboard ('" + handlers.get(caller).id
                        + "') with scoreboard ('" + id + "') for " + caller.getName());
            this.json = registeredScoreboards.get(id);
            this.caller = caller;
            this.id = id;
            man = ScoreboardManager.getInstance("scoreboard_" + id);
            handlers.put(caller, this);
        }

        private void begin() throws Exception {
            Bukkit.getPluginManager().registerEvents(this, lobby);
            if (caller.getScoreboard() != null && caller.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) { // player already got a scorebaord and it's not the main scoreboard
                EntityUtils.getInstance().setMetadata(caller, "scoreboard_" + id, caller.getScoreboard()); // save this scoreboard in metadata
            }
            parse();
            reloadScoreboard();
        }

        private void done() {
            done(true);
        }

        private void done(boolean remove) {
            if (done)
                return;
            done = true;
            HandlerList.unregisterAll(this);
            ticker.interrupt();
            man.getScoreboard(caller).getObjective(DisplaySlot.SIDEBAR).unregister();
            if (remove)
                handlers.remove(caller, this);
        }

        private void parse() throws Exception {
            title = json.getString("title");
            updateIntervall = json.getDouble("updateIntervall");
            for (int i = 15; i > 0; i--) {
                String content = json.getString("content." + i);
                if (content == null || content.equals(""))
                    continue;

                String body = McStringUtils.randomMcColorString(4) + "§r";

                scores.put(new SidebarElement("", body, content), i);
                simpleScores.put(body, i);
            }
        }

        private void reloadScoreboard() {
            man.setSidebar(caller, Lobby.replaceC(caller, title), simpleScores);
            if (ticker != null)
                ticker.interrupt();
            ticker = new Thread() {
                @Override
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            Lobby.debug("Scoreboard Title: \"" + Lobby.replaceC(caller, title) + "\"");
                            caller.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(Lobby.replaceC(caller, title));

                            for (SidebarElement element : scores.keySet()) {

                                String prefix = "";
                                String body = element.getBody();
                                String suffix = Lobby.replaceC(caller, element.getSuffix());

                                if (suffix.length() > 16) {
                                    prefix = suffix.substring(0, 16);
                                    if (prefix.endsWith("§"))
                                        prefix = prefix.substring(0, prefix.length() - 1);
                                    suffix = suffix.substring(prefix.length());


                                    String codes = "";
                                    Pattern p = Pattern.compile("§.");
                                    Matcher m = p.matcher(prefix);
                                    while (m.find()) {
                                        String colorCode = m.group(0);
                                        if (!codes.contains(colorCode))
                                            codes += colorCode;
                                    }
                                    suffix = codes + suffix;
                                    if (suffix.length() > 16)
                                        suffix = suffix.substring(0, 16);
                                }

                                Lobby.debug("Scoreboard Element: \"" + element.getPrefix() + element.getSuffix() + "\" -> \"" + prefix + suffix + "\"");

                                man.updateSidebarEntry(caller, element.getBody(), body, prefix, suffix);
                            }

                            Thread.sleep((long) (updateIntervall * 1000D));
                        } catch (InterruptedException e) {
                            interrupt();
                        }
                    }
                }
            };
            ticker.start();
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            if (e.getPlayer() == caller)
                done();
        }
    }
}
