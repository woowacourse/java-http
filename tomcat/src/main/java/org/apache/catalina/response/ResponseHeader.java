package org.apache.catalina.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {
    public static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";

    private final Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public void setAccept(String value) {
        headers.put(CONTENT_TYPE, value);
    }

    public void setContentLength(String value) {
        headers.put(CONTENT_LENGTH, value);
    }

    public void setCookie(String value) {
        headers.put(COOKIE, value);
    }

    public void setLocation(String value) {
        headers.put(LOCATION, value);
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append(" \r\n"));

        return response.toString();
    }
}
