package org.apache.coyote.http11.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UriParser {

    private static final String DELIMITER = "?";
    private static final String QUERY_DELIMITER = "&";
    private static final String QUERY_NAME_DELIMITER = "=";

    private final String path;
    private final Map<String, String> query;

    public UriParser(String uri) {
        this.path = getPath(uri);
        this.query = getQuery(uri);
    }

    private String getPath(String uri) {
        return uri.substring(0, uri.indexOf(DELIMITER));
    }

    private Map<String, String> getQuery(String uri) {
        String[] query = uri.substring(uri.indexOf(DELIMITER) + 1)
                .split(QUERY_DELIMITER);
        return Arrays.stream(query)
                .map(element -> element.split(QUERY_NAME_DELIMITER))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public String find(String queryName) {
        return query.get(queryName);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQuery() {
        return query;
    }
}
