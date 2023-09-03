package org.apache.coyote.http11.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class QueryString {

    private static final String SEPARATOR = "&";
    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryStrings;

    public QueryString(final Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryString from(final String queryString) {
        return Arrays.stream(queryString.split(SEPARATOR))
                .map(query -> query.split(DELIMITER))
                .collect(collectingAndThen(
                        toUnmodifiableMap(query -> query[KEY_INDEX], query -> query[VALUE_INDEX]),
                        QueryString::new
                ));
    }

    public static QueryString empty() {
        return new QueryString(Collections.emptyMap());
    }

    public String getValue(final String key) {
        return this.queryStrings.get(key);
    }

}
