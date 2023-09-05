package org.apache.coyote.http11.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;

public class HttpParser {

    private static final String QUERY_STRING_START_SYMBOL = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_SEPARATOR = "; ";

    private HttpParser() {
    }

    public static Map<String, String> parseQueryParameters(String uri) {
        if (uri.contains(QUERY_STRING_START_SYMBOL)) {
            final var queryStartIndex = uri.indexOf(QUERY_STRING_START_SYMBOL);
            final var queryString = uri.substring(queryStartIndex + 1);
            return parseParameters(queryString);
        }
        return Collections.emptyMap();
    }

    private static Map<String, String> parseParameters(String queryString) {
        return Arrays.stream(queryString.split(PARAMETER_SEPARATOR))
                .map(parameter -> parameter.split(KEY_VALUE_DELIMITER))
                .filter(keyValuePair -> keyValuePair.length == 2)
                .collect(Collectors.toMap(keyValuePair -> keyValuePair[0],
                        keyValuePair -> keyValuePair[1]));
    }

    public static String parsePath(String uri) {
        return uri.split("\\?")[0];
    }

    public static List<HttpCookie> parseCookies(String cookieLine) {
        final var cookies = cookieLine.split(COOKIE_SEPARATOR);
        return Arrays.stream(cookies)
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER))
                .map(pair -> new HttpCookie(pair[0], pair[1]))
                .collect(Collectors.toList());
    }

    public static Map<String, String> parseFormData(String formData) {
        return Arrays.stream(formData.split(PARAMETER_SEPARATOR))
                .map(parameter -> parameter.split(KEY_VALUE_DELIMITER))
                .filter(keyValuePair -> keyValuePair.length == 2)
                .collect(Collectors.toMap(keyValuePair -> keyValuePair[0],
                        keyValuePair -> keyValuePair[1]));
    }
}
