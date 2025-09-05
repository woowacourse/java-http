package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUrl {

    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequestUrl(String url) {
        String[] tokens = url.split("\\?");
        this.path = tokens[0];
        this.queryParams = new HashMap<>();
        if (tokens.length == 2) {
            this.queryParams.putAll(parseQueryString(tokens[1]));
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        return Arrays.stream(queryString.split("&")).map(s -> s.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    public boolean equalPath(String path) {
        return this.path.equals(path);
    }

    public boolean isStaticResourcePath() {
        return this.path.matches(".+\\..+");
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String key) {
        return queryParams.get(key);
    }
}
