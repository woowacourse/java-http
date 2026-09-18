package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestUri {

    private static final String ROOT_PATH = "/";
    private static final String STATIC_TARGET_PATH = "static";

    private final String path;
    private final Map<String, String> queryParameters;

    private HttpRequestUri(String path, Map<String, String> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static HttpRequestUri from(String requestUri) {
        int index = requestUri.indexOf("?");
        if (index == -1) {
            return new HttpRequestUri(requestUri, Map.of());
        }
        String path = requestUri.substring(0, index) + ".html";
        Map<String, String> queryParameters = parseQueryParameter(requestUri.substring(index + 1));

        return new HttpRequestUri(path, queryParameters);
    }

    public boolean isRoot() {
        return ROOT_PATH.equals(path);
    }

    public boolean isParameterEmpty() {
        return queryParameters.isEmpty();
    }

    public String getParameter(String key) {
        return queryParameters.get(key);
    }

    public String getResourcePath() {
        return STATIC_TARGET_PATH + path;
    }

    private static Map<String, String> parseQueryParameter(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(queryString, "&");

        while (stringTokenizer.hasMoreTokens()) {
            String parameter = stringTokenizer.nextToken();
            int index = parameter.indexOf("=");
            String key = parameter.substring(0, index);
            String value = parameter.substring(index + 1);
            queryParameters.put(key, value);
        }
        return queryParameters;
    }
}
