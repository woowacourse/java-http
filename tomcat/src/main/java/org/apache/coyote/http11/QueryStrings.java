package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStrings {

    private static final String DELIMITER = "=";
    private static final String QUERY_STRING_DELIMITER = "&";

    private Map<String, String> values;

    public QueryStrings(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryStrings from(final String rawString) {
        final Map<String, String> values = new HashMap<>();

        if (rawString == null || rawString.isBlank()) {
            return new QueryStrings(values);
        }

        final String[] queryStrings = rawString.split(QUERY_STRING_DELIMITER);

        for (final String queryString : queryStrings) {
            final int index = queryString.indexOf(DELIMITER);
            final String name = queryString.substring(0, index);
            final String value = queryString.substring(index + 1);

            values.put(name, value);
        }

        return new QueryStrings(values);
    }

    public boolean exists() {
        return !values.isEmpty();
    }

    public String getValueByName(final String name) {
        if(values.containsKey(name)) {
            return values.get(name);
        }

        throw new IllegalArgumentException("해당 이름이 존재하지 않습니다.");
    }

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }
}
