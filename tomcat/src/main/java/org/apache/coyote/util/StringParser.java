package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public class StringParser {

    public static String parseQueryString(String uri){
        return uri.split("\\?")[1];
    }

    public static Map<String, String> parseQueryParameter(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            }
        }
        return params;
    }
}
