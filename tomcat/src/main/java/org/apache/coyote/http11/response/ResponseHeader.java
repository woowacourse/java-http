package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.coyote.http11.MimeType;

public class ResponseHeader {

    private final String LOCATION = "Location";
    private final String CONTENT_TYPE = "Content-Type";
    private final String CONTENT_LENGTH = "Content-Length";
    private final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> header;

    public ResponseHeader() {
        this.header = new HashMap<>();
    }

    public void setLocation(String location) {
        addHeader(LOCATION, location);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void setContentType(MimeType mimeType) {
        addHeader(CONTENT_TYPE, mimeType.getContentType());
    }

    public String toHeaderString() {
        StringJoiner headerJoiner = new StringJoiner("\r\n");
        for (Map.Entry<String, String> entry : header.entrySet()) {
            headerJoiner.add(entry.getKey() + ": " + entry.getValue() + " ");
        }
        return headerJoiner.toString();
    }

    public void setContentLength(String contentLength) {
        addHeader(CONTENT_LENGTH, contentLength);
    }

    public void setCookie(String cookie) {
        addHeader(SET_COOKIE, cookie);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "header=" + header +
                '}';
    }
}
