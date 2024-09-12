package org.apache.catalina.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String HEADER_END_SEPARATOR = " \r\n";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";
    public static final String HEADER_KEY_VALUE_SEPARATOR = ": ";

    private final Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public void setContentType(String value) {
        headers.put(CONTENT_TYPE, value);
    }

    public void setContentLength(String value) {
        headers.put(CONTENT_LENGTH, value);
    }

    public void setCookie(String value) {
        headers.put(COOKIE, value);
    }

    public void setRedirection(String value) {
        headers.put(LOCATION, value);
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    @Override

    public String toString() {
        StringBuilder response = new StringBuilder();
        headers.forEach(
                (key, value) -> response.append(key)
                        .append(HEADER_KEY_VALUE_SEPARATOR)
                        .append(value)
                        .append(HEADER_END_SEPARATOR));

        return response.toString();
    }
}
