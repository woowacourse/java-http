package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.coyote.CharsetType;
import org.apache.coyote.MimeType;

public class ResponseHeader {

    private static final String CHARSET = "; charset=";
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
        String contentType = mimeType.getMimeType();
        if (mimeType.isTextBased()) {
            contentType += CHARSET + CharsetType.UTF_8.getCharset();
        }
        addHeader(CONTENT_TYPE, contentType);
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
