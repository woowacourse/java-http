package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> extraContents;
    private final Map<String, String> queryStrings;
    private final Cookie cookie;

    private HttpRequestHeader(
            HttpMethod method,
            String path,
            Map<String, String> extraContents,
            Map<String, String> queryStrings,
            Cookie cookie
    ) {
        this.method = method;
        this.path = path;
        this.extraContents = extraContents;
        this.queryStrings = queryStrings;
        this.cookie = cookie;
    }

    public static HttpRequestHeader of(String method, String path, String[] extraContents) {
        Map<String, String> parseExtraContents = parseExtraContents(extraContents);
        return new HttpRequestHeader(
                HttpMethod.from(method),
                getUrlRemovedQueryString(path),
                parseExtraContents,
                parseQueryStrings(path),
                parseCookies(parseExtraContents.get("Cookie"))
        );
    }

    private static Map<String, String> parseExtraContents(String[] extraContents) {
        Map<String, String> parseResultOfExtraContents = new HashMap<>();

        for (String component : extraContents) {
            putParseResultOfComponent(parseResultOfExtraContents, component, ":");
        }

        return parseResultOfExtraContents;
    }

    private static void putParseResultOfComponent(
            Map<String, String> parseResultOfRequestBody,
            String component,
            String delimiter
    ) {
        if (component.length() < 2) {
            return;
        }

        String[] keyAndValue = component.split(delimiter);
        parseResultOfRequestBody.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    private static String getUrlRemovedQueryString(String path) {
        int questionMarkIndex = path.lastIndexOf("?");

        if (questionMarkIndex == -1) {
            return path;
        }

        return path.substring(0, questionMarkIndex);
    }

    private static Map<String, String> parseQueryStrings(String path) {
        Map<String, String> parseResultOfQueryStrings = new HashMap<>();
        int questionMarkIndex = path.lastIndexOf("?");

        if (questionMarkIndex == -1) {
            return parseResultOfQueryStrings;
        }

        String queryString = path.substring(questionMarkIndex + 1);
        String[] queryComponent = queryString.split("&");

        for (String component : queryComponent) {
            putParseResultOfComponent(parseResultOfQueryStrings, component, "=");
        }

        return parseResultOfQueryStrings;
    }

    private static Cookie parseCookies(String cookies) {
        return Cookie.from(cookies);
    }

    public String get(String key) {
        return extraContents.getOrDefault(key, "");
    }

    public Cookie getCookies() {
        return cookie;
    }

    public String getQueryString(String key) {
        return queryStrings.getOrDefault(key, "");
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

}
