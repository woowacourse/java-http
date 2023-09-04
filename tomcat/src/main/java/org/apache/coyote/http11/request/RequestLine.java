package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;

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

        final int index = uri.indexOf("?");
        if (index == -1) {
            return new RequestLine(method, uri, Map.of());
        }

        final String path = uri.substring(0, index);
        final String queryString = uri.substring(index + 1);
        final Map<String, String> query = Arrays.stream(queryString.split("&"))
                .map(str -> str.split("="))
                .collect(Collectors.toMap(
                        e -> e[0],
                        e -> e[1]
                ));

        return new RequestLine(method, path, query);
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(final String queryKey) {
        return query.get(queryKey);
    }
}
