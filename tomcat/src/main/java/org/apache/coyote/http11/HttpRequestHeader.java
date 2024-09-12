package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record HttpRequestHeader(
        String httpMethod,
        String path,
        Map<String, String> queryString,
        Map<String, String> headers) {

    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";

    public HttpRequestHeader(String request) {
        this(
                extractHttpMethod(request),
                extractPath(request),
                extractQueryString(request),
                extractHeaders(request)
        );
    }

    private static String extractHttpMethod(String request) {
        return request
                .split(System.lineSeparator())[0]
                .split(" ")[0];
    }

    private static String extractPath(String request) {
        String url = request
                .split(System.lineSeparator())[0]
                .split(" ")[1];

        return url.split("[?]")[0];
    }

    private static Map<String, String> extractQueryString(String request) {
        String url = request
                .split(System.lineSeparator())[0]
                .split(" ")[1];

        Map<String, String> map = new HashMap<>();
        if (url.contains("?")) {
            String[] queryStringArgs = url
                    .split("[?]")[1]
                    .split("&");
            Arrays.stream(queryStringArgs)
                    .map(param -> param.split("="))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> map.put(parts[0], parts[1]));
        }
        return map;
    }

    private static Map<String, String> extractHeaders(String requestHeader) {
        String[] lines = requestHeader.split(System.lineSeparator());
        return Arrays.stream(lines)
                .skip(1)
                .map(line -> line.split(": "))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    public boolean hasContentLength(){
        return headers.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public String getContentType() {
        return headers.get(CONTENT_TYPE);
    }

    public Map<String, String> getCookies() {
        String cookie = headers.get(COOKIE);
        if (cookie == null) {
            return null;
        }

        return Arrays.stream(cookie.split(";"))
                .map(String::trim)
                .map(part -> part.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getPath() {
        return this.path;
    }

    public String getQueryStringValue(String key) {
        return this.queryString.get(key);
    }
}
