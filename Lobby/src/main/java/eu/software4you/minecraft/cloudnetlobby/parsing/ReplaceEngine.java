package eu.software4you.minecraft.cloudnetlobby.parsing;

import eu.software4you.minecraft.cloudnetlobby.Lobby;
import eu.software4you.minecraft.cloudnetlobby.addons.Placeholder;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceEngine {
    public static String fullReplace(Player caller, String source) {
        return replaceExpressions(caller, placeholderApiReplace(caller, source));
    }

    private static String replaceExpressions(Player caller, String source) {
        Pattern pattern = Pattern.compile("\\$\\{[^$\\{}]*\\}");
        while (pattern.matcher(source).find()) {
            Matcher matcher = pattern.matcher(source);
            while (matcher.find()) {
                String expression = matcher.group(0);
                source = source.replace(expression, replaceExpression(caller, expression));
            }
        }

        return source.replace("$!§", "$");
    }

    private static String replaceExpression(final Player caller, final String expression) {
        //String expression = expression.substring(expression.indexOf("${"), expression.lastIndexOf("}")+1);
        String expressionBody = replaceExpressions(caller, expression.substring(2, expression.length() - 1));
        /*String id = expressionBody.substring(0, expressionBody.contains(":") ? expressionBody.indexOf(":") : expressionBody.length());
        String arg = expressionBody.substring(id.length() + (expressionBody.contains(":") ? 1 : 0));*/
        Map.Entry<String, String> e = Lobby.parseAddonCall(expressionBody);
        String id = e.getKey();
        String arg = e.getValue();

        String result = "$!§{" + expressionBody + "}";

        if (id.startsWith("?") && (StringUtils.containsOneOfArray(id, "=", "<", ">")) && arg.startsWith("(") && arg.endsWith(")")) {
            String seperator;
            if (id.contains(">="))
                seperator = ">=";
            else if (id.contains(">"))
                seperator = ">";
            else if (id.contains("<"))
                seperator = "<";
            else if (id.contains("<="))
                seperator = "<=";
            else if (id.contains("!="))
                seperator = "!=";
            else
                seperator = "=";


            String[] strs = id.substring(1).split(seperator);
            String partA = replaceExpressions(caller, strs[0]);
            String partB = replaceExpressions(caller, strs[1]);

            boolean expressionResult = false;

            switch (seperator) {
                case ">=":
                    expressionResult = Integer.valueOf(partA) >= Integer.valueOf(partB);
                    break;
                case ">":
                    expressionResult = Integer.valueOf(partA) > Integer.valueOf(partB);
                    break;
                case "<=":
                    expressionResult = Integer.valueOf(partA) <= Integer.valueOf(partB);
                    break;
                case "<":
                    expressionResult = Integer.valueOf(partA) < Integer.valueOf(partB);
                    break;
                case "=":
                    expressionResult = partA.equals(partB);
                    break;
                case "!=":
                    expressionResult = !partA.equals(partB);
                    break;
            }
            if (expressionResult) {
                result = replaceExpressions(caller, arg.substring(1, arg.contains(")else(") ? arg.indexOf(")else(") : arg.length() - 1));
            } else {
                result = arg.contains(")else(") ? replaceExpressions(caller, arg.substring(arg.indexOf(")else(") + 6, arg.length() - 1)) : "";
            }
        } else if (Placeholder.isRegistered(id)) {
            result = Placeholder.replace(caller, id, arg);
        }

        return expression.replace(expression, result);
    }

    private static String placeholderApiReplace(Player caller, String source) {
        if (caller != null && ClassUtils.isClass("me.clip.placeholderapi.PlaceholderAPI"))
            source = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(caller, source);
        return source;
    }
}
