package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String path;
    private Map<String, String> queryParams;

    private HttpRequest(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest of(final String uri) {
        String[] readLine = uri.split(" ");
        String path = getPath(readLine[1]);
        Map<String, String> queryParams = getQueryParams(readLine[1]);
        return new HttpRequest(path, queryParams);
    }

    public static String getPath(String input) {
        if (!input.contains("?")) {
            return input;
        }
        return input.substring(0, input.lastIndexOf("?"));
    }

    public static Map<String, String> getQueryParams(final String path) {
        Map<String, String> queryParams = new HashMap<>();
        if (!path.contains("?")) {
            return queryParams;
        }

        String queryString = path.substring(path.lastIndexOf("?") + 1);
        String[] queryParam = queryString.split("&");
        for (String param : queryParam) {
            String[] split1 = param.split("=");
            queryParams.put(split1[0], split1[1]);
        }
        return queryParams;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
