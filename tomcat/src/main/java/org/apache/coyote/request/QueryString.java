package org.apache.coyote.request;

import org.apache.exception.QueryParamsNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {
    private static final Map<String, String> EMPTY = Collections.emptyMap();

    private final Map<String, String> queryStrings;

    public QueryString(final Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryString from(final String query) {
        if (!query.contains("?")) {
            return new QueryString(EMPTY);
        }
        final Map<String, String> queryMap = getKeyAndValue(query);
        return new QueryString(queryMap);
    }

    private static Map<String, String> getKeyAndValue(final String query) {
        final Map<String, String> queryMap = new LinkedHashMap<>();
        final String queryWithoutDelimiter = query.substring(1);

        Arrays.stream(queryWithoutDelimiter.split("&"))
                .forEach(string -> queryMap.put(string.split("=")[0], string.split("=")[1]));
        return queryMap;
    }

    public String getQueryValueBy(final String key){
        if(!queryStrings.containsKey(key)) {
            throw new QueryParamsNotFoundException();
        }
        return queryStrings.get(key);
    }

    @Override
    public String toString() {
        if (queryStrings.isEmpty()) {
            return "";
        }
        return "?" + queryStrings.keySet()
                .stream()
                .map(key -> key + "=" + queryStrings.get(key))
                .collect(Collectors.joining("&"));
    }
}
