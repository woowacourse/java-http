package org.apache.coyote.http.response;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.MimeType;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader extends Header {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    public ResponseHeader() {
        super();
    }

    private ResponseHeader(Map<String, String> headers) {
        super(headers);
    }

    public void setContentType(String contentType) {
        setHeader(CONTENT_TYPE, contentType);
    }

    public void setContentLength(long contentLength) {
        setHeader(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(String location) {
        setHeader(LOCATION, location);
    }

    public void setCookie(String cookie) {
        setHeader(SET_COOKIE, cookie);
    }

    public static ResponseHeader basicResponseHeader(int length) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MimeType.HTML.getContentType());
        headers.put(CONTENT_LENGTH, String.valueOf(length));
        return new ResponseHeader(headers);
    }
}
