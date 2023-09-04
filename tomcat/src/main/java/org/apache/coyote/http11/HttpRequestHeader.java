package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private static final Map<String, String> requestUrlToFilePath = Map.of(
            "/login", "/login.html",
            "/register", "/register.html"
    );

    private HttpMethod method;
    private String path;
    private Map<String, String> extraContents;
    private Map<String, String> queryStrings;
    private Map<String, String> cookies;

    private HttpRequestHeader(
            HttpMethod method,
            String path,
            Map<String, String> extraContents,
            Map<String, String> queryStrings,
            Map<String, String> cookies
    ) {
        this.method = method;
        this.path = path;
        this.extraContents = extraContents;
        this.queryStrings = queryStrings;
        this.cookies = cookies;
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

    private static Map<String, String> parseExtraContents(String[] extraContents) {
        Map<String, String> parseResultOfExtraContents = new HashMap<>();

        for (String component : extraContents) {
            putParseResultOfComponent(parseResultOfExtraContents, component, ":");
        }

        return parseResultOfExtraContents;
    }

    private static Map<String, String> parseCookies(String cookies) {
        if (cookies == null) {
            return Collections.emptyMap();
        }

        Map<String, String> parseResultOfCookies = new HashMap<>();

        for (String cookie : cookies.split(";")) {
            putParseResultOfComponent(parseResultOfCookies, cookie, "=");
        }

        return parseResultOfCookies;
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

    public String getContentType() {
        return "text/" + extractExtension();
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

    public String get(String key) {
        return extraContents.getOrDefault(key, "");
    }

    public Map<String, String> getCookies() {
        return cookies;
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

    public Map<String, String> getExtraContents() {
        return extraContents;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

}
