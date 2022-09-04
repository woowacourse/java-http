package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean match(Pattern uriPattern) {
        Matcher matcher = uriPattern.matcher(path);
        return matcher.matches();
    }

    public String getPath() {
        return path;
    }

    public Object getParameter(String key) {
        return queryParams.get(key);
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
