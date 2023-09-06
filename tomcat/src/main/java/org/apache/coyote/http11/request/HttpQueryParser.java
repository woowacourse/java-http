package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpQueryParser {

    private HttpQueryParser() {
    }

    public static Map<String, String> parse(String path) {
        String query = getQuery(path);
        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String, String> queries = new HashMap<>();
        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);
            queries.put(key, value);
        }

        return queries;
    }

    private static String getQuery(String path) {
        int index = path.indexOf("?");
        if (index == -1) {
            return null;
        }
        return path.substring(index + 1);
    }
}
