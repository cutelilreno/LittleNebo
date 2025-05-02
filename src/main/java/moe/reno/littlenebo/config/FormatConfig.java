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

/**
 * Handles format settings. These are loaded from the plugin conf.
 */
public record FormatConfig (String format, String legacyFormat, String groupPermission) {

    /**
     * Creates a validated FormatConfig with fallback to default if invalid.
     */
    public static FormatConfig createValidated(
            String format, 
            String legacyFormat, 
            String groupPermission) {
            
        // Do not check for {display_name} in case they use PlaceholderAPI
        if (format == null || !format.contains("{message}")) {
            return new FormatConfig("{display_name}: {message}", "", "");
        }
        legacyFormat = ""; // remove as it is deprecated
        return new FormatConfig(format, legacyFormat, groupPermission);
    }

    /**
     * Checks if this format is permission-gated.
     *
     * @return true if they have perms
     */
    public boolean hasPermission() {
        return groupPermission != null && !groupPermission.isEmpty();
    }
}