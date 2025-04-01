/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024-2025 Tunahan Sezen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.jenkins.plugins.adminparameters;

import hudson.markup.MarkupFormatter;
import jenkins.model.Jenkins;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private Utils() {
    }

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    private static final String DESC_WARNING_MSG = "This is an admin parameter";

    public static boolean isUserAdmin() {
        return Jenkins.get().hasPermission(Jenkins.ADMINISTER);
    }

    public static String adminFormattedDescription(String description) {
        StringBuilder sb = new StringBuilder();
        if (isHtmlFormatter()) {
            sb.append("<span style='color:#FF0000;'>");
        }
        sb.append(DESC_WARNING_MSG);
        if (isHtmlFormatter()) {
            sb.append("</span>");
        }
        sb.append("<br>");
        sb.append(description);
        return sb.toString();
    }

    private static boolean isHtmlFormatter() {
        MarkupFormatter formatter = Jenkins.get().getMarkupFormatter();
        String formatterClassName = formatter.getClass().getSimpleName();
        LOGGER.log(Level.CONFIG, "Formatter class name: {0}", formatterClassName);
        return formatterClassName.toLowerCase().contains("html");
    }

}
