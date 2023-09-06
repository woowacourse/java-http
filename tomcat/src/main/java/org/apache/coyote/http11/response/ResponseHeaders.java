package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";
    private static final String DELIMITER = ": ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";


    private final Map<String, String> headers;
    private final ResponseCookie cookie;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
        this.cookie = new ResponseCookie();
    }

    public void setContentType(final String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setContentLength(final int length) {
        headers.put(CONTENT_LENGTH, String.valueOf(length));
    }

    public void setLocation(final String location) {
        headers.put(LOCATION, location);
    }

    public void setCookie(final String cookieName, final String value) {
        cookie.setAttribute(cookieName, value);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (cookie.isPresent()) {
            sb.append(cookie.getHeaderString());
            sb.append(CRLF);
        }
        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey() + DELIMITER + header.getValue() + SPACE + CRLF);
        }
        return sb.toString();
    }
}
