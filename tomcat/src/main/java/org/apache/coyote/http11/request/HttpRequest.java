package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }

    public static HttpRequest ofRequestLine(String line) {
        return new HttpRequest(RequestLine.of(line), null);
    }

    public Map<String, String> getQueries() {
        int index = getResource().indexOf("?");
        String queryString = getResource().substring(index + 1);
        String[] keyValues = queryString.split("&");

        Map<String, String> queryMap = new HashMap<>();
        for (String keyValue : keyValues) {
            String key = keyValue.split("=")[0];
            String value = keyValue.split("=")[1];
            queryMap.put(key, value);
        }
        return queryMap;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getContentType() {
        return requestLine.getContentType();
    }

    public String getResource() {
        return requestLine.getResource();
    }

    public boolean hasQueryString() {
        return getResource().contains("?");
    }
}
