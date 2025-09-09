package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryParser {

    private static final String AND = "&";
    private static final String EQUAL = "=";

    public Map<String, String> parse(final String uri) {
        String[] pairs = uri.split(AND);

        Map<String, String> queries = new LinkedHashMap<>();
        for (String pair : pairs) {
            int equalIndex = pair.indexOf(EQUAL);
            String name = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            queries.put(name, value);
        }

        return queries;
    }
}
