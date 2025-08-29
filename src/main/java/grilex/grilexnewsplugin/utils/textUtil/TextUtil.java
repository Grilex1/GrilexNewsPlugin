package grilex.grilexnewsplugin.utils.textUtil;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final Pattern HTML_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})(.)");
    private static final Pattern SIMPLE_HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#>|<#([A-Fa-f0-9]{6})>(.)");
    private static final Pattern BBCODE_COLOR_PATTERN = Pattern.compile("\\[COLOR=#([A-Fa-f0-9]{6})](.*?)\\[/COLOR]");

    public String colorize(String message) {
        if (message == null || message.isEmpty()) return message;

        message = processJson(message);
        message = processBBCode(message);
        message = processHtmlColors(message);
        message = processMinecraftHex(message);
        message = processSimpleHex(message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public List<String> colorizeList(List<String> messages) {
        if (messages == null || messages.isEmpty()) return messages;
        List<String> colored = new ArrayList<>();
        for (String line : messages) {
            colored.add(colorize(line));
        }
        return colored;
    }

    private String processJson(String text) {
        if (text.trim().startsWith("{")) {
            try {
                TextComponent component = (TextComponent) ComponentSerializer.parse(text)[0];
                return component.toLegacyText();
            } catch (Exception ignored) {
                return text;
            }
        }
        return text;
    }

    private String processBBCode(String text) {
        Matcher matcher = BBCODE_COLOR_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            String content = matcher.group(2);
            String minecraftHex = "&x&" + String.join("&", hex.split(""));
            matcher.appendReplacement(result, minecraftHex + content);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String processHtmlColors(String text) {
        Matcher matcher = HTML_COLOR_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            String character = matcher.group(2);
            String minecraftHex = "&x&" + String.join("&", hex.split(""));
            matcher.appendReplacement(result, minecraftHex + character);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String processMinecraftHex(String text) {
        return text.replaceAll("&x", "§x");
    }

    private String processSimpleHex(String text) {
        Matcher matcher = SIMPLE_HEX_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
            String content = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);
            String minecraftHex = "&x&" + String.join("&", hex.split(""));
            matcher.appendReplacement(result, minecraftHex + content);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
