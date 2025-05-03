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
package moe.reno.littlenebo.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FormatConfig class
 */
public class FormatConfigTest {

    @Test
    public void createValidated_validFormat_returnsUnchanged() {
        String format = "{display_name}: {message}";
        FormatConfig config = FormatConfig.createValidated(format, "", "permission.test");
        
        assertEquals(format, config.format());
        assertEquals("", config.legacyFormat());
        assertEquals("permission.test", config.groupPermission());
    }
    
    @Test
    public void createValidated_missingMessagePlaceholder_returnsDefaultFormat() {
        // Format without {message} placeholder should be replaced with default
        String invalidFormat = "This format has no message placeholder";
        FormatConfig config = FormatConfig.createValidated(invalidFormat, "", "permission.test");
        
        assertEquals("{display_name}: {message}", config.format());
        assertEquals("", config.legacyFormat());
        assertEquals("", config.groupPermission());
    }
    
    @Test
    public void createValidated_nullFormat_returnsDefaultFormat() {
        FormatConfig config = FormatConfig.createValidated(null, "", "permission.test");
        
        assertEquals("{display_name}: {message}", config.format());
        assertEquals("", config.legacyFormat());
        assertEquals("", config.groupPermission());
    }
    
    @Test
    public void hasPermission_withGroupPermission_returnsTrue() {
        FormatConfig config = new FormatConfig("format", "", "permission.test");
        assertTrue(config.hasPermission());
    }
    
    @Test
    public void hasPermission_withEmptyGroupPermission_returnsFalse() {
        FormatConfig config = new FormatConfig("format", "", "");
        assertFalse(config.hasPermission());
    }
    
    @Test
    public void hasPermission_withNullGroupPermission_returnsFalse() {
        FormatConfig config = new FormatConfig("format", "", null);
        assertFalse(config.hasPermission());
    }
}