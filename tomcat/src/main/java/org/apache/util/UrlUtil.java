package org.apache.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

    private static final Pattern PATH_PATTERN = Pattern.compile("(?<path>/[a-zA-Z0-9\\-./]*)[?]?");
    private static final Pattern QUERY_PARAMETER_PATTERN
            = Pattern.compile("((?<field>[a-zA-Z0-9\\-%.]+)=(?<value>[a-zA-Z0-9\\-%.]+))&?");

    public static String joinUrl(String... paths) {
        return String.join("/", paths)
                .replaceAll("/+", "/");
    }

    public static String parsePath(String pathString) {
        Matcher matcher = PATH_PATTERN.matcher(pathString);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Failed to parse path : " + pathString);
        }
        return matcher.group("path");
    }

    public static Map<String, String> parseQueryString(String queryString) {
        Matcher matcher = QUERY_PARAMETER_PATTERN.matcher(queryString);
        Map<String, String> queryParameters = new HashMap<>();
        while (matcher.find()) {
            queryParameters.put(decode(matcher.group("field")), decode(matcher.group("value")));
        }
        return queryParameters;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
