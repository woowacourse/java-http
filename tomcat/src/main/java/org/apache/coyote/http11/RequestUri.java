package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestUri {

    private final String path;
    private final Map<String, String> queryParams;

    public RequestUri(String requestUri) {
        int index = requestUri.indexOf("?");
        if (index == -1) {
            this.path = requestUri;
            this.queryParams = new HashMap<>();
            return;
        }

        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);

        this.path = path;
        this.queryParams = toQueryMap(queryString);
    }

    private Map<String, String> toQueryMap(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
