package org.apache.util;

public class UrlUtil {
    public static String joinUrl(String... paths) {
        return String.join("/", paths)
                .replaceAll("/+", "/");
    }
}
