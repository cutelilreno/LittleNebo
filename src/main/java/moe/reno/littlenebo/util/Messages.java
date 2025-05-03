/**
 * MIT License
 * Copyright (c) 2025 cutelilreno
 * https://opensource.org/licenses/MIT
 */
package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Utility class for creating styled message components. Created for code readability.
 * Provides methods for creating error, success, title, and info messages.
 */
public class Messages {
    /**
     * Creates a red-colored error message component.
     *
     * @param text The message text
     * @return A Component with the text styled as an error message
     */
    public static Component error(String text) {
        return Component.text(text, NamedTextColor.RED);
    }

    /**
     * Creates a green-colored success message component.
     *
     * @param text The message text
     * @return A Component with the text styled as a success message
     */
    public static Component success(String text) {
        return Component.text(text, NamedTextColor.GREEN);
    }

    /**
     * Creates a gold-colored title message component.
     *
     * @param text The message text
     * @return A Component with the text styled as a title
     */
    public static Component title(String text) {
        return Component.text(text, NamedTextColor.GOLD);
    }

    /**
     * Creates a gray-colored info message component.
     *
     * @param text The message text
     * @return A Component with the text styled as an info message
     */
    public static Component info(String text) {
        return Component.text(text, NamedTextColor.GRAY);
    }
}
