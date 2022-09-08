package org.apache.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

    private StringUtil() {
    }

    public static List<String> splitAsList(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter));
    }

    public static String readOneLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read line from BufferedReader", e);
        }
    }

    public static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }
}
