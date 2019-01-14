package eu.software4you.minecraft.cloudnetlobby.parsing;

import eu.software4you.minecraft.ItemAPI;
import org.apache.commons.configuration2.JSONConfiguration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ItemVariabler {
    private final JSONConfiguration json;
    private final String prefix;
    private final Player caller;

    public ItemVariabler(JSONConfiguration json, String prefix) {
        this(json, prefix, null);
    }

    public ItemVariabler(JSONConfiguration json, String prefix, Player caller) {
        this.json = json;
        this.prefix = prefix;
        this.caller = caller;
    }

    public void iteration(final String id, final int startSlot) throws Exception {
        final String key = prefix + "." + id;
        int slot = startSlot;

        String var = "";
        String cmd = "";
        String arg = "";

        if (id.contains("${") && id.contains("}")) {
            String expr = id.substring(id.indexOf("${") + 2, id.indexOf("}"));
            var = expr.substring(0, expr.indexOf(";"));
            String cmdExpr = expr.substring(var.length() + 1);
            cmd = cmdExpr.substring(0, cmdExpr.contains(":") ? cmdExpr.indexOf(":") : cmdExpr.length());
            arg = cmdExpr.substring(cmd.length() + (cmdExpr.contains(":") ? 1 : 0));
        }


        Variabler v = new Variabler(json, caller, var);
        for (String s : list(cmd, arg)) {
            v.setPrefix(key + ".item.");
            v.setVarRepl(s);

            int material = v.jsonInt("id");
            int durability = v.jsonInt("durability");
            int amount = v.jsonInt("amount");
            String name = v.jsonString("name");
            List<String> lore = v.jsonStringList("lore");
            ArrayList<ItemAPI.EntchantmentStore> enchantments = new ArrayList<>();
            for (String ench : v.jsonStringList("enchantments")) {
                if (ench == null || ench.equals(""))
                    continue;
                String enchantmentKey = ench.substring(0, ench.indexOf(":"));
                int enchantmentLevel = Integer.valueOf(ench.substring(ench.indexOf(":") + 1));
                Enchantment enchantment = ItemAPI.getEnchantmentByKey(enchantmentKey);
                if (enchantment == null)
                    throw new Exception("Could not find Enchantment " + enchantmentKey);
                enchantments.add(new ItemAPI.EntchantmentStore(enchantment, enchantmentLevel));
            }
            Material mat = ItemAPI.getMaterialById(material);
            if (mat == null)
                throw new Exception("Could not find Material " + id);
            ItemStack stack = ItemAPI.genItem(mat, name, lore, durability, amount, true, enchantments);

            v.setPrefix(key + ".");
            String action = "";
            List<String> leftaction = v.jsonStringList("clickaction.left");
            List<String> rightaction = v.jsonStringList("clickaction.right");

            item(slot, stack);
            actionLeftClick(slot, leftaction);
            actionRightClick(slot, rightaction);

            slot++;
        }
    }

    private List<String> list(String id, String arg) {
        if (!eu.software4you.minecraft.cloudnetlobby.addons.List.isRegistered(id))
            return new ArrayList<>(Arrays.asList("$"));
        return eu.software4you.minecraft.cloudnetlobby.addons.List.list(caller, id, arg);
    }

    public abstract void item(int slot, ItemStack stack);

    public abstract void actionLeftClick(int slot, List<String> actions);

    public abstract void actionRightClick(int slot, List<String> actions);


}
