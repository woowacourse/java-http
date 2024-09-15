package org.apache.coyote.http11.response;

import org.apache.coyote.http11.MimeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeader {

    private static final String CRLF = "\r\n";
    private static final String KEY_DELIMITER = ": ";
    private static final String SPACE_SEPARATOR = " ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";

    private final Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setContentType(MimeType contentType) {
        addHeader(CONTENT_TYPE, contentType.getType());
    }

    public void setContentLength(String contentLength) {
        addHeader(CONTENT_LENGTH, contentLength);
    }

    public void setCookie(String cookie) {
        addHeader(SET_COOKIE, cookie);
    }

    public void setLocation(String location) {
        addHeader(LOCATION, location);
    }

    public String getContents() {
        StringJoiner stringJoiner = new StringJoiner(CRLF);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey() + KEY_DELIMITER + entry.getValue() + SPACE_SEPARATOR);
        }
        return stringJoiner.toString();
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
