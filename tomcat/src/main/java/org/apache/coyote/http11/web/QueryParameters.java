package org.apache.coyote.http11.web;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int VALUE_NOT_EXISTS_SIZE = 1;
    private static final String KEY_VALUE_SPLIT_DELIMITER = "=";
    private static final String QUERY_STRING_SPLIT_DELIMITER = "&";
    private static final String EMPTY_STRING = "";

    private final Map<String, String> values;

    public QueryParameters(final Map<String, String> values) {
        this.values = values;
    }

    public static QueryParameters from(final String queryString) {
        final String[] rawQueryParameters = queryString.split(QUERY_STRING_SPLIT_DELIMITER);
        final Map<String, String> values = Arrays.stream(rawQueryParameters)
                .map(it -> it.split(KEY_VALUE_SPLIT_DELIMITER))
                .collect(Collectors.toMap(
                        it -> it[KEY_INDEX],
                        QueryParameters::getOrDefault)
                );

        return new QueryParameters(values);
    }

    private static String getOrDefault(final String[] keyValue) {
        if (keyValue.length == VALUE_NOT_EXISTS_SIZE) {
            return EMPTY_STRING;
        }
        return keyValue[VALUE_INDEX];
    }

    public String getValueByKey(final String key) {
        return values.getOrDefault(key, EMPTY_STRING);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryParameters)) return false;
        final QueryParameters that = (QueryParameters) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
