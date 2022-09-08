package org.apache.util;

public class NumberUtil {
    public static int parseIntSafe(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long parseLongSafe(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
