package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpUri {

    private final String path;
    private final Map<String, Object> queryParams;

    private HttpUri(String path, Map<String, Object> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpUri of(String uri) {
        validate(uri);

        String path = uri;
        Map<String, Object> queryParams = new HashMap<>();

        int index = uri.indexOf("?");
        if (index != -1) {
            path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);

            queryParams = getParameters(queryString);
        }

        return new HttpUri(path, queryParams);
    }

    private static void validate(String uri) {
        if (!uri.startsWith("/")) {
            throw new IllegalArgumentException("올바르지 않은 URI 입니다!");
        }
    }

    private static Map<String, Object> getParameters(String queryString) {
        String[] queries = queryString.split("&");

        Map<String, Object> parameters = new HashMap<>();
        for (String query : queries) {
            int index = query.indexOf("=");
            String key = query.substring(0, index);
            String value = query.substring(index + 1);

            parameters.put(key, value);
        }

        return parameters;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpUri httpUri = (HttpUri) o;
        return Objects.equals(path, httpUri.path) && Objects.equals(queryParams, httpUri.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, queryParams);
    }
}
