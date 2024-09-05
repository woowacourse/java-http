package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryString;
    private final Map<String, String> headers;

    public HttpRequestHeader(String requestHeader) {
        String[] firstLineArgs = requestHeader.split(System.lineSeparator())[0].split(" ");
        this.httpMethod = firstLineArgs[0];
        this.path = initializePath(firstLineArgs[1]);
        this.queryString = initializeQueryString(firstLineArgs[1]);
        this.headers = initializeHeaders(requestHeader);
    }

    private String initializePath(String url) {
        if (url.contains("?")) {
            return url.split("[?]")[0];
        }

        return url;
    }

    private Map<String, String> initializeQueryString(String url) {
        Map<String, String> map = new HashMap<>();
        if (url.contains("?")) {
            String[] queryStringArgs = url.split("[?]")[1].split("[&=]");
            for (int i = 0; i < queryStringArgs.length / 2; i++) {
                map.put(queryStringArgs[i * 2], queryStringArgs[i * 2 + 1]);
            }
        }

        return map;
    }

    private Map<String, String> initializeHeaders(String requestHeader) {
        Map<String, String> map = new HashMap<>();
        String[] lines = requestHeader.split(System.lineSeparator());
        for (int i = 1; i < lines.length; i++) {
            String[] headerArgs = lines[i].split(": ");
            map.put(headerArgs[0], headerArgs[1]);
        }

        return map;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
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

    public String getHeader(String key) {
        return headers.get(key);
    }
}
