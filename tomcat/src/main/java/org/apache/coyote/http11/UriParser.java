package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class UriParser {

    public static Map<String, String> parseUri(String uri) {
        Map<String, String> queryValues = new HashMap<>();
        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);

        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] split = query.split("=");
            queryValues.put(split[0], split[1]);
        }
        return queryValues;
    }
}
