package org.apache.util;

public class IntegerUtil {
    public static int parseIntSafe(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
