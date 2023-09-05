package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private final String requestMethod;
    private final String path;
    private final Map<String, String> query;

    private RequestLine(String requestMethod, String path, Map<String, String> query) {
        this.requestMethod = requestMethod;
        this.path = path;
        this.query = query;
    }

    public static RequestLine from(String line) {
        String[] splitLine = line.split(" ");

        String method = splitLine[0];
        String uri = splitLine[1];

        int index = uri.indexOf("?");

        if (index == -1 ) {
            return new RequestLine(method, uri, Map.of());
        }

        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        Map<String, String> query = Arrays.stream(queryString.split("&"))
                .map(str -> str.split("="))
                .collect(Collectors.toMap(
                        s -> s[0],
                        s -> s[1]
                ));
        return new RequestLine(method, path, query);
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String queryKey) {
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
