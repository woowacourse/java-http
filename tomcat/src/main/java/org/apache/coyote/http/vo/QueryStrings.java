package org.apache.coyote.http.vo;

import java.util.Arrays;
import util.MultiValueMap;

public class QueryStrings {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final MultiValueMap<String, String> queryStrings;

    private QueryStrings(MultiValueMap<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryStrings from(final String rawQueryStrings) {
        return new QueryStrings(parseQueryStrings(rawQueryStrings));
    }

    private static MultiValueMap<String, String> parseQueryStrings(final String rawQueryStrings) {
        MultiValueMap<String, String> queryStrings = new MultiValueMap<>();

        Arrays.stream(rawQueryStrings.split(QUERY_STRING_DELIMITER)).forEach(
                it -> {
                    String[] keyWithValue = it.split(KEY_VALUE_DELIMITER);
                    queryStrings.put(keyWithValue[KEY_INDEX], keyWithValue[VALUE_INDEX]);
                }
        );

        return queryStrings;
    }

    public String getQueryString(final String key) {
        return queryStrings.getRecentValue(key);
    }

    public static QueryStrings getEmptyQueryStrings(){
        return new QueryStrings(new MultiValueMap<>());
    }
}
