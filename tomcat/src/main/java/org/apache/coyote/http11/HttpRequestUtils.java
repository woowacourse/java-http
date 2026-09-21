package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtils {
    private static final int ELEMENT_SIZE = 2;

    public static Map<String, String> parseCookieHeader(String cookieHeader) {
        return parseString(";", cookieHeader);
    }

    public static Map<String, String> parseQueryString(String queryString) {
        return parseString("&", queryString);
    }

    private static Map<String, String> parseString(String delimiter, String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        String[] queryList = queryString.trim().split(delimiter);
        for (String str : queryList) {
            String[] query = str.trim().split("=");
            if (query.length < ELEMENT_SIZE) {
                continue;
            }
            params.put(query[0], query[1]);
        }
        return params;
    }
}
