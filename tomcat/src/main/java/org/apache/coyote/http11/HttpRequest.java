package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final Map<String, String> requestUrlToFilePath = Map.of(
            "/login", "/login.html"
    );

    public HttpMethod method;
    public String path;
    public Map<String, String> queryStrings;

    private HttpRequest(
            HttpMethod method,
            String path,
            Map<String, String> queryStrings
    ) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static HttpRequest of(String method, String path) {
        return new HttpRequest(
                HttpMethod.from(method),
                getUrlRemovedQueryString(path),
                parseQueryString(path)
        );
    }

    private static String getUrlRemovedQueryString(String path) {
        int questionMarkIndex = path.lastIndexOf("?");

        if (questionMarkIndex == -1) {
            return path;
        }

        return path.substring(0, questionMarkIndex);
    }

    private static Map<String, String> parseQueryString(String path) {
        Map<String, String> parseResultOfQueryString = new HashMap<>();
        int questionMarkIndex = path.lastIndexOf("?");

        if (questionMarkIndex == -1) {
            return parseResultOfQueryString;
        }

        String queryString = path.substring(questionMarkIndex + 1);
        String[] queryComponent = queryString.split("&");

        for (String component : queryComponent) {
            String[] keyAndValue = component.split("=");
            parseResultOfQueryString.put(keyAndValue[0], keyAndValue[1]);
        }

        return parseResultOfQueryString;
    }

    public String getFilePath() {
        return requestUrlToFilePath.getOrDefault(path, path);
    }

    public String extractExtension() {
        int lastIndexOfComma = path.lastIndexOf(".");

        if (lastIndexOfComma == -1) {
            return "html";
        }

        return path.substring(lastIndexOfComma + 1);
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

}
