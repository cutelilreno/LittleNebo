package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Messages {
    public static Component error(String text) {
        return Component.text(text, NamedTextColor.RED);
    }

    public static Component success(String text) {
        return Component.text(text, NamedTextColor.GREEN);
    }

    public static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD);
    }
    public static Component info(String text) {
        return Component.text(text, NamedTextColor.GRAY);
    }
}
