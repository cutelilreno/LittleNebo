/**
 * MIT License
 *
 * Copyright (c) 2025 cutelilreno
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package moe.reno.littlenebo.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Messages utility class
 */
class MessagesTest {

    @Test
    void error_withMessage_returnsRedComponent() {
        Component component = Messages.error("Error message");
        assertEquals("Error message", ColorUtil.componentToString(component));
        assertEquals(NamedTextColor.RED, component.color());
    }

    @Test
    void success_withMessage_returnsGreenComponent() {
        Component component = Messages.success("Success message");
        assertEquals("Success message", ColorUtil.componentToString(component));
        assertEquals(NamedTextColor.GREEN, component.color());
    }

    @Test
    void title_withMessage_returnsGoldComponent() {
        Component component = Messages.title("Title message");
        assertEquals("Title message", ColorUtil.componentToString(component));
        assertEquals(NamedTextColor.GOLD, component.color());
    }

    @Test
    void info_withMessage_returnsGrayComponent() {
        Component component = Messages.info("Info message");
        assertEquals("Info message", ColorUtil.componentToString(component));
        assertEquals(NamedTextColor.GRAY, component.color());
    }
}