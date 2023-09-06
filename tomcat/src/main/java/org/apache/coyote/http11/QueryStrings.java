package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStrings {

    private static final String PATH_QUERY_STRING_DELIMITER = "\\?";
    private static final String PATH_QUERY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> queryStrings;

    public static QueryStrings empty() {
        return new QueryStrings("");
    }

    public QueryStrings(String fullPath) {
        if (fullPath.contains("?")) {
            var queryParams = fullPath.split(PATH_QUERY_STRING_DELIMITER)[1];
            this.queryStrings = Arrays.stream(queryParams.split(PATH_QUERY_DELIMITER))
                                      .map(queryString ->
                                              {
                                                  final var keyValue = queryString.split(KEY_VALUE_DELIMITER);
                                                  return Map.entry(keyValue[0], keyValue[1]);
                                              }
                                      )
                                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return;
        }
        this.queryStrings = Collections.emptyMap();
    }

    public boolean hasQueryStrings() {
        return !queryStrings.isEmpty();
    }

    public String getValue(String key) {
        return queryStrings.get(key);
    }
}
