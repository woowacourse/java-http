package org.apache.coyote.http11.message.request;

import java.util.HashMap;
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

    private final Map<String, String> query = new HashMap<>();

    public QueryString(final String queryString) {
        parseQueryString(queryString);
    }

    private void parseQueryString(final String queryString) {
        for (String singleQuery : queryString.split(QUERY_STRING_DELIMITER)) {
            String[] splitQuery = singleQuery.split(KEY_VALUE_DELIMITER);
            String key = splitQuery[KEY_INDEX];
            String value = splitQuery[VALUE_INDEX];
            query.put(key, value);
        }
    }

    public Optional<String> getQuery(final String key) {
        String value = query.get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}
