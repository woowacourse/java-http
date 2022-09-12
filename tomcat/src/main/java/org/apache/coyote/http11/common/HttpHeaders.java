package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import utils.ParseUtils;

public class HttpHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String REGEX_1 = "\r\n";
    private static final String REGEX_2 = ": ";
    private static final String DEFAULT_LENGTH = "0";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final String headers) {
        return new HttpHeaders(ParseUtils.parse(headers, REGEX_1, REGEX_2));
    }

    public String getContentType() {
        return headers.get(CONTENT_TYPE);
    }

    public String getContentLength() {
        return headers.getOrDefault(CONTENT_LENGTH, DEFAULT_LENGTH);
    }

    public String getLocation() {
        return headers.get(LOCATION);
    }

    public boolean hasCookies() {
        return headers.containsKey(COOKIE);
    }

    public String getCookies() {
        return headers.get(COOKIE);
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType + ";charset=utf-8");
    }

    public void setContentLength(int contentLength) {
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        headers.put(LOCATION, location);
    }

    public void setCookie(String cookie) {
        headers.put(SET_COOKIE, cookie);
    }

    public String toMessage() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
