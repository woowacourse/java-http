package org.apache.coyote.http11.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String KEY_VALUE_SPLIT_DELIMITER = "=";
    private static final String QUERY_STRING_SPLIT_DELIMITER = "&";

    private final Map<String, String> values;

    public QueryParameters(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryParameters from(final String queryString) {
        final Map<String, String> values = Arrays.stream(queryString.split(QUERY_STRING_SPLIT_DELIMITER))
                .map(it -> it.split(KEY_VALUE_SPLIT_DELIMITER))
                .collect(Collectors.toMap(
                        it -> it[KEY_INDEX],
                        it -> it[VALUE_INDEX])
                );

        return new QueryParameters(values);
    }

    public String getValueByKey(final String key) {
        return values.get(key);
    }
}
