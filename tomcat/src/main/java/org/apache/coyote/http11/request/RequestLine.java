package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String QUERY_PARAM_START_SIGN = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SIGN = "=";
    private static final int QUERY_KEY_INDEX = 0;
    private static final int QUERY_VALUE_INDEX = 1;

    private final String requestMethod;
    private final String path;
    private final Map<String, String> query;

    private RequestLine(
            final String requestMethod,
            final String path,
            final Map<String, String> query
    ) {
        this.requestMethod = requestMethod;
        this.path = path;
        this.query = query;
    }

    public static RequestLine from(final String line) {
        final String[] lineSplit = line.split(DELIMITER);

        final String method = lineSplit[METHOD_INDEX];
        final String uri = lineSplit[URI_INDEX];

        final int index = uri.indexOf(QUERY_PARAM_START_SIGN);
        if (index == -1) {
            return new RequestLine(method, uri, Map.of());
        }

        final String path = uri.substring(0, index);
        final String queryString = uri.substring(index + 1);
        final Map<String, String> query = Arrays.stream(queryString.split(QUERY_PARAMETER_DELIMITER))
                .map(str -> str.split(QUERY_PARAM_KEY_VALUE_SIGN))
                .collect(Collectors.toMap(
                        e -> e[QUERY_KEY_INDEX],
                        e -> e[QUERY_VALUE_INDEX]
                ));

        return new RequestLine(method, path, query);
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(final String queryKey) {
        return query.get(queryKey);
    }

    public boolean containsQuery() {
        return !query.isEmpty();
    }

    public boolean containsQuery(final String key) {
        return query.containsKey(key);
    }

    public String getMethod() {
        return requestMethod;
    }
}
