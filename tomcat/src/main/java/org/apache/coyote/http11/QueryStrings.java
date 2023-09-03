package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStrings {

    private static final String PATH_QUERY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> queryStrings;

    public QueryStrings(String fullPath) {
        this.queryStrings = Arrays.stream(fullPath.split(PATH_QUERY_DELIMITER))
                                  .map(queryString ->
                                          {
                                              final var keyValue = queryString.split(KEY_VALUE_DELIMITER);
                                              return Map.entry(keyValue[0], keyValue[1]);
                                          }
                                  )
                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getValue(String key) {
        return queryStrings.get(key);
    }
}
