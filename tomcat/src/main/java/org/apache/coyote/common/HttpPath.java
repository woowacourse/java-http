package org.apache.coyote.common;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpPath {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String path;
    private final Map<String, List<String>> queryStrings;

    private HttpPath(String path) {
        this.path = path;
        this.queryStrings = Collections.emptyMap();
    }

    private HttpPath(String path, Map<String, List<String>> queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static HttpPath from(String uri) {
        int index = uri.indexOf("?");
        if (index <= 0) {
            return new HttpPath(uri);
        }
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        return new HttpPath(path, parseQuery(queryString));
    }

    private static Map<String, List<String>> parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(query.split("&"))
            .map(queries -> queries.split("="))
            .collect(groupingBy(queries -> queries[KEY_INDEX], mapping(HttpPath::parseQueries, toList())));
    }

    private static String parseQueries(String[] queries) {
        if (queries.length > 1) {
            return queries[VALUE_INDEX];
        }
        return "";
    }

    public List<String> getQueryString(String key) {
        return queryStrings.getOrDefault(key, Collections.emptyList());
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQueryStrings() {
        return queryStrings;
    }
}
