package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestUri {

    private final String path;
    private final Map<String, String> queryStrings;

    private RequestUri(String path, Map<String, String> queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static RequestUri from(String requestUri) {
        int index = requestUri.indexOf("?");

        if (index == -1) {
            return new RequestUri(requestUri, new HashMap<>());
        }

        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);

        Map<String, String> queryStrings = Stream.of(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(parts -> parts[0], strings -> strings[1]));

        return new RequestUri(path, queryStrings);
    }

    public String findQueryStringValue(String key) {
        return queryStrings.get(key);
    }

    public boolean isQueryStringExisted() {
        return !queryStrings.isEmpty();
    }

    public String getPath() {
        return path;
    }
}
