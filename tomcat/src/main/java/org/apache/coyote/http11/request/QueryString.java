package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {
    public static final QueryString EMPTY = new QueryString(new HashMap<>());
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String,String> queryStrings;

    private QueryString(final Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryString from(String queryString) {
        Map<String, String> queryStrings = Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(splitQueryString -> splitQueryString.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        splitQuery -> splitQuery[KEY_INDEX],
                        splitQuery -> splitQuery[VALUE_INDEX]
                ));

        return new QueryString(queryStrings);
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }
}
