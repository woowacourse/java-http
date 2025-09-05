package org.apache.coyote.http11.parser;

public class UriParser {
    public static final String QUERY_SEPARATOR = "?";
    public static final String ROOT_PATH = "/";
    public static final String EXTENSION_SEPARATOR = ".";

    public static String extractPath(String uri) {
        int index = uri.indexOf(QUERY_SEPARATOR);
        return uri.substring(0, index);
    }

    public static String extractQueryString(String uri) {
        int index = uri.indexOf(QUERY_SEPARATOR);
        return uri.substring(index + 1);
    }

    public static String extractExtension(String uri) {
        int index = uri.lastIndexOf(EXTENSION_SEPARATOR);
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    public static boolean hasQuery(String uri) {
        return uri.contains(QUERY_SEPARATOR);
    }

    public static boolean isRootPath(String uri) {
        return uri.equals(ROOT_PATH);
    }
}
