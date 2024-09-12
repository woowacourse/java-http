package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String COOKIE = "Cookie";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public static HttpHeaders from(List<String> headers) {
        Map<String, String> headersMap = headers.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));

        return new HttpHeaders(headersMap);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return 0;
        }

        String contentLength = headers.get(CONTENT_LENGTH);
        return Integer.parseInt(contentLength);
    }

    public String getCookie() {
        if (!headers.containsKey(COOKIE)) {
            return "";
        }

        return headers.get(COOKIE);
    }

    public String buildHttpHeadersResponse() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
