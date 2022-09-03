package org.apache.catalina.utils;

public class Parser {
    public static String removeBlank(final String input) {
        return input.replaceAll(" ", "");
    }
}
