package org.apache.coyote.support;

import java.util.HashMap;
import java.util.Map;

public class Parser {

    public static String parseUri(final String line) {
        return line.split(" ")[1];
    }

    public static Map<String, String> parseQueryString(final String parsedURI) {
        int index = parsedURI.indexOf("?");
        String queryString = parsedURI.substring(index + 1);
        String[] keyValues = queryString.split("&");

        Map<String, String> queryMap = new HashMap<>();
        for (String keyValue : keyValues) {
            String key = keyValue.split("=")[0];
            String value = keyValue.split("=")[1];
            queryMap.put(key, value);
        }
        return queryMap;
    }
}
