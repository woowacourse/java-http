package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class Parameters {

    private static final String QUERY_DELIMITER = "&";
    private static final String ENTRY_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> entries;

    public Parameters(final Map<String, String> entries) {
        this.entries = entries;
    }

    public static Parameters empty() {
        return new Parameters(emptyMap());
    }

    public static Parameters from(final char[] buffer) {
        final Map<String, String> parsed = parse(String.valueOf(buffer));
        return new Parameters(parsed);
    }

    private static Map<String, String> parse(final String queries) {
        final Map<String, String> result = new HashMap<>();

        Arrays.stream(queries.split(QUERY_DELIMITER))
                .map(query -> query.split(ENTRY_DELIMITER))
                .forEach(query -> result.put(query[KEY_INDEX], query[VALUE_INDEX]));

        return result;
    }

    public String getParameter(final String key) {
        return entries.get(key);
    }
}
