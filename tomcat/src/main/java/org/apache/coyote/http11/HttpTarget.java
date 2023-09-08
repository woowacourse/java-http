package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTarget {

    private static final String EMPTY_VALUE = "";

    private final String path;
    private final Map<String, String> queries;

    public HttpTarget(final String target) {
        this.path = removeQueriesFrom(target);
        this.queries = getQueriesFrom(target);
    }

    private static Map<String, String> getQueriesFrom(String target) {
        int queryStringStart = target.indexOf('?');
        if (queryStringStart == -1) {
            return Collections.emptyMap();
        }
        String rawQueryString = target.substring(queryStringStart + 1);
        String[] rawQueries = rawQueryString.split("&");
        return Arrays.stream(rawQueries)
                .collect(Collectors.toMap(
                        rawQuery -> rawQuery.split("=")[0],
                        rawQuery -> rawQuery.split("=")[1]
                ));
    }

    private static String removeQueriesFrom(final String target) {
        int queryStringStart = target.indexOf('?');
        if (queryStringStart == -1) {
            return target;
        }
        return target.substring(0, queryStringStart);
    }

    public String getPath() {
        return path;
    }

    public String getQuery(final String name) {
        return queries.getOrDefault(name, EMPTY_VALUE);
    }

    public Map<String, String> getQueries() {
        return new HashMap<>(queries);
    }
}
