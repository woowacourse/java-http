package org.apache.coyote.http11.message.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.ToString;

@ToString
public class QueryString {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private QueryString(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryString parse(final String rawQueryString) {
        if (Objects.isNull(rawQueryString) || rawQueryString.isBlank()) {
            return new QueryString(Map.of());
        }

        Map<String, String> queries = parseQueryString(rawQueryString);
        return new QueryString(queries);
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        Map<String, String> queries = new LinkedHashMap<>();

        for (String query : queryString.split(QUERY_STRING_DELIMITER)) {
            String[] splitQuery = query.split(KEY_VALUE_DELIMITER);
            queries.put(splitQuery[KEY_INDEX], splitQuery[VALUE_INDEX]);
        }

        return queries;
    }

    public Optional<String> getValues(final String key) {
        String value = values.get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}
