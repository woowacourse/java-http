package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidQueryStringFormatException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final String DELIMITER = "&";
    private static final String KEY_VALUE_SPLITTER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> queries;

    private QueryString(final Map<String, String> queries) {
        this.queries = queries;
    }

    public static QueryString from(final String queryStrings) {
        try {
            final Map<String, String> queries = Arrays.stream(queryStrings.split(DELIMITER))
                    .map(query -> query.split(KEY_VALUE_SPLITTER))
                    .collect(Collectors.toMap(
                            keyAndValue -> keyAndValue[KEY_INDEX],
                            keyAndValue -> keyAndValue[VALUE_INDEX]
                    ));
            return new QueryString(queries);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidQueryStringFormatException();
        }
    }

    public static QueryString empty() {
        return new QueryString(Collections.emptyMap());
    }

    public String getValue(final String key) {
        return queries.get(key);
    }

    public boolean isEmpty() {
        return queries.isEmpty();
    }
}
